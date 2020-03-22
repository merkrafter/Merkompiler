package com.merkrafter.parsing;

import com.merkrafter.lexing.*;
import com.merkrafter.representation.*;
import com.merkrafter.representation.ast.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

import static com.merkrafter.lexing.TokenType.*;

/****
 * This class can be used to parse tokens.
 * To start parsing, call the parse() method which triggers a recursive descent.
 *
 * @since v0.2.0
 * @author merkrafter
 ***************************************************************/
public class Parser {
    // ATTRIBUTES
    //==============================================================
    /**
     * The scanner that provides the tokens for this parser
     */
    @NotNull
    private final Scanner scanner;

    /**
     * The base symbol table that encloses all others.
     */
    @NotNull
    private SymbolTable symbolTable;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new Parser based on a scanner that provides tokens.
     ***************************************************************/
    public Parser(@NotNull final Scanner scanner) {
        this(scanner, null);
    }

    /**
     * Creates a new Parser with a set of global variables. Can be used for testing purposes.
     */
    Parser(@NotNull final Scanner scanner, @Nullable final SymbolTable globalVariables) {
        this.scanner = scanner;
        this.scanner.processToken();
        symbolTable = new SymbolTable(globalVariables);
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Parses the tokens given by the underlying token iterator.
     *
     * @return the AST of the program
     */
    @NotNull
    public AbstractSyntaxTree parse() {
        final AbstractSyntaxTree ast = parseClass();
        if (ast instanceof ErrorNode) {
            return ast;
        }

        final Token sym = scanner.getSym();
        if (sym.getType() != EOF) {
            return new ErrorNode(generateErrorMessage("<EOF>"));
        }

        return ast;
    }

    @NotNull AbstractSyntaxTree parseClass() {
        final Token sym = scanner.getSym();
        if (!(sym instanceof KeywordToken && ((KeywordToken) sym).getKeyword() == Keyword.CLASS)) {
            return new ErrorNode(generateErrorMessage("'class' keyword"));
        }
        scanner.processToken();
        final Position position = sym.getPosition();

        final IdentNode identifier = parseIdentifier();
        if (identifier == null) {
            return new ErrorNode(generateErrorMessage("class name"));
        }

        final ClassDescription clazz =
                new ClassDescription(identifier.getIdentifier(), symbolTable);

        final SymbolTable prevSymbolTable = symbolTable;
        symbolTable = clazz.getSymbolTable();

        // reads all methods and variables into the symbol table
        final boolean success;
        try {
            success = parseClassBody();
        } catch (@NotNull final ParserException e) {
            return new ErrorNode(e.getMessage());
        }

        // do not return immediately in case of errors, because the symbol table scope must be reset
        symbolTable = prevSymbolTable;
        if (success) {
            return new ClassNode(clazz, position);
        } else {
            return new ErrorNode("Error while parsing the class body");
        }
    }

    boolean parseClassBody() throws ParserException {
        if (scanner.getSym().getType() == L_BRACE) {
            scanner.processToken();
            if (parseDeclarations()) {
                if (scanner.getSym().getType() == R_BRACE) {
                    scanner.processToken();
                    return true;
                }
            }

        }
        return false;
    }

    boolean parseDeclarations() throws ParserException {
        // final declaration
        while (parseFinalDeclaration()) ;
        // type declaration
        while (parseLocalDeclaration()) ;
        // method declaration
        while (parseMethodDeclaration()) ;
        return true;
    }

    /**
     * Tries to parse a final declaration and returns whether the next tokens match the
     * grammar: final_declaration = final type ident "=" expression ";".
     * <p>
     * If this succeeds, the variable is pushed into the symbol table.
     *
     * @return whether the next tokens represent a final declaration
     */
    // this method is final because it is not an official rule of the grammar but only a helper
    private boolean parseFinalDeclaration() throws ParserException {
        final Token sym = scanner.getSym();
        if (!(sym instanceof KeywordToken && ((KeywordToken) sym).getKeyword() == Keyword.FINAL)) {
            return false;
        }
        scanner.processToken();

        final Type type = parseType();
        if (type == null) {
            return false;
        }
        final IdentNode identifier = parseIdentifier();
        if (identifier == null) {
            return false;
        }
        if (scanner.getSym().getType() != ASSIGN) {
            return false;
        }
        scanner.processToken();
        final Expression value = parseExpression();

        if (scanner.getSym().getType() != SEMICOLON) {
            return false;
        }
        scanner.processToken();

        final VariableDescription var =
                new VariableDescription(identifier.getIdentifier(), type, value, true);
        final boolean wasInserted = symbolTable.insert(var);
        if (!wasInserted) {
            throw new ParserException(String.format("Variable %s was declared multiple times",
                                                    identifier));
        }

        return true;
    }

    /**
     * Tries to parse a method declaration. In case of success, the method is stored in the symbol
     * table as well as all variables that are declared in that method.
     *
     * @return whether the method could be parsed completely and was stored in the symbol table
     */
    boolean parseMethodDeclaration() throws ParserException {
        // get procedure prototype
        final ActualProcedureDescription procedureDescription = parseMethodHead();
        if (procedureDescription == null) {
            return false;
        }

        // set a new scope of the symbol table
        final SymbolTable prevSymbolTable = symbolTable;
        symbolTable = procedureDescription.getSymbols();
        for (final VariableDescription varDesc : procedureDescription.getParamList()) {
            final boolean wasInserted = symbolTable.insert(varDesc);
            if (!wasInserted) {
                throw new ParserException(String.format(
                        "Formal parameter %s was declared multiple times in procedure %s",
                        varDesc.getName(),
                        procedureDescription.getName()));
            }
        }

        final Statement statements = parseMethodBody();
        procedureDescription.setEntrypoint(statements);

        // set the symbol table back to the previous scope
        symbolTable = prevSymbolTable;

        if (statements instanceof ErrorNode) {
            throw new ParserException(statements.toString());
        }

        final boolean wasInserted = symbolTable.insert(procedureDescription);
        if (!wasInserted) {
            throw new ParserException(String.format("Procedure %s was declared multiple times",
                                                    procedureDescription.getName()));
        }

        return true;
    }

    /**
     * Tries to parse a method head.
     *
     * @return procedureDescription or null if an error occurred
     */
    @Nullable ActualProcedureDescription parseMethodHead() {
        final Token sym = scanner.getSym();
        if (!(sym instanceof KeywordToken
              && ((KeywordToken) scanner.getSym()).getKeyword() == Keyword.PUBLIC)) {
            return null;
        }
        scanner.processToken();
        final Position position = sym.getPosition();

        final Type type = parseMethodType();
        if (type == null) {
            return null;
        }

        final IdentNode identifier = parseIdentifier();
        if (identifier == null) {
            return null;
        }

        final List<VariableDescription> formalParameters = parseFormalParameters();
        if (formalParameters == null) {
            return null;
        }
        return new ActualProcedureDescription(type,
                                              identifier.getIdentifier(),
                                              formalParameters,
                                              symbolTable,
                                              position);
    }

    /**
     * Tries to parse a method type. Methods in JavaSST can only return INT or VOID.
     *
     * @return type of a method or null if no applicable type was found
     */
    @Nullable Type parseMethodType() {
        final Token sym = scanner.getSym();
        if (!(sym instanceof KeywordToken)) {
            return null;
        }
        scanner.processToken();

        switch (((KeywordToken) sym).getKeyword()) {
            case VOID:
                return Type.VOID;
            case INT:
                return Type.INT;
            default:
                return null;
        }
    }

    /**
     * Tries to parse formal parameters.
     *
     * @return list of variable descriptions found or null if an error occurred
     */
    @Nullable List<VariableDescription> parseFormalParameters() {
        Token sym = scanner.getSym();
        if (sym.getType() != L_PAREN) {
            return null;
        }
        scanner.processToken();

        final List<VariableDescription> formalParameters = new LinkedList<>();
        VariableDescription var = parseFpSection();
        // having no variableDescription is okay, but the closing paren must still be validated
        if (var != null) {
            formalParameters.add(var);
            while (scanner.getSym().getType() == COMMA) {
                scanner.processToken();

                var = parseFpSection();
                if (var == null) {
                    // after a comma, var must not be null
                    return null;
                }
                formalParameters.add(var);
            }
        }

        sym = scanner.getSym();
        if (sym.getType() != R_PAREN) {
            return null;
        }
        scanner.processToken();

        return formalParameters;
    }

    /**
     * Tries to parse an fp_section.
     *
     * @return a variable description for the formal parameter or null if an error occurs
     */
    @Nullable VariableDescription parseFpSection() {
        final Type type = parseType();
        if (type == null) {
            return null;
        }

        final IdentNode identifier = parseIdentifier();
        if (identifier == null) {
            return null;
        }

        // assumes that variables can only be integers
        return new VariableDescription(identifier.getIdentifier(), type, 0, false);
    }

    /**
     * Tries to parse a method body.
     *
     * @return whether this operation was successful
     */
    @NotNull Statement parseMethodBody() throws ParserException {
        Token sym = scanner.getSym();
        if (sym.getType() != L_BRACE) {
            return new ErrorNode(generateErrorMessage("{"));
        }
        scanner.processToken();

        // only iterate through them; they're stored in the symbolTable
        while (parseLocalDeclaration()) ;

        final Statement statements = parseStatementSequence();

        sym = scanner.getSym();
        if (sym.getType() != R_BRACE) {
            return new ErrorNode(generateErrorMessage("}"));
        }
        scanner.processToken();

        return statements;
    }

    /**
     * Tries to parse a local declaration and returns whether the next 3 tokens match the
     * grammar: local_declaration = type ident ";".
     * <p>
     * If this succeeds, the variable is pushed into the symbol table.
     *
     * @return whether the next tokens represent a local declaration
     */
    boolean parseLocalDeclaration() throws ParserException {
        final Type type = parseType();
        if (type == null) {
            return false;
        }
        final IdentNode identifier = parseIdentifier();
        if (identifier == null) {
            return false;
        }
        if (scanner.getSym().getType() != SEMICOLON) { // no need to store this in a variable
            return false;
        }
        scanner.processToken();

        final VariableDescription var = new VariableDescription(identifier.getIdentifier(),
                                                                type,
                                                                type.getDefaultValue(),
                                                                false);
        final boolean wasInserted = symbolTable.insert(var);
        if (!wasInserted) {
            throw new ParserException(String.format("Variable %s was declared multiple times",
                                                    identifier));
        }
        return true;
    }

    @NotNull Statement parseStatementSequence() {
        final Statement headNode = parseStatement();
        if (headNode instanceof ErrorNode) {
            return headNode;
        }
        Statement last = headNode;
        Statement current = parseStatement();
        while (!(current instanceof ErrorNode)) {
            last.setNext(current);
            last = current;
            current = parseStatement();
        }
        return headNode;
    }

    /**
     * Tries to parse a statement according to the
     * grammar: statement = assignment | procedure_call | if_statement | while_statement | return_statement.
     * It then returns a node that represents this statement.
     * Returns an error node if a syntax error occurs.
     *
     * @return ASTBaseNode representing this statement or ErrorNode
     */
    @NotNull Statement parseStatement() {
        // factoring of
        // statement = ident '=' expression ';' | ident actual_parameters ';'
        //             ^ assignment               ^ procedure call
        Statement node = parseStatementForAssignmentOrProcedureCall();
        if (!(node instanceof ErrorNode)) {
            return node;
        }
        node = parseIfStatement();
        if (!(node instanceof ErrorNode)) {
            return node;
        }
        node = parseWhileStatement();
        if (!(node instanceof ErrorNode)) {
            return node;
        }
        node = parseReturnStatement();
        if (!(node instanceof ErrorNode)) {
            return node;
        }
        return new ErrorNode(generateErrorMessage("statement"));
    }

    /**
     * This method is a helper for differentiating between assignments or procedure calls.
     * It is needed because both start with an IDENT token.
     *
     * @return AssignmentNode, ProcedureCallNode, or ErrorNode
     */
    @NotNull
    private Statement parseStatementForAssignmentOrProcedureCall() {
        final IdentNode identifier = parseIdentifier();
        if (identifier == null) {
            // both an assignment and a procedure call need an identifier first
            return new ErrorNode(generateErrorMessage("identifier"));
        }

        // try parsing an assignment
        final Expression expression = parseAssignmentWithoutIdent();
        if (!(expression instanceof ErrorNode)) {
            final VariableDescription var =
                    (VariableDescription) symbolTable.find(identifier.getIdentifier(),
                                                           (Type[]) null);
            if (var == null) {
                return new ErrorNode(String.format("Reference to unknown variable %s", identifier));
            }
            final VariableAccessNode varNode =
                    new VariableAccessNode(var, identifier.getPosition());
            return new AssignmentNode(varNode, expression);
        }

        // begin parsing a procedure call
        final ParameterListNode parameters = parseActualParameters();
        if (parameters == null) {
            return new ErrorNode(generateErrorMessage("parameter list"));
        }
        final Token sym = scanner.getSym();
        if (sym.getType() != SEMICOLON) {
            return new ErrorNode(generateErrorMessage("';'"));
        }
        // this actually is a procedure call
        scanner.processToken();

        return new ProcedureCallNode(new ProcedureDescriptionProxy(identifier.getIdentifier(),
                                                                   parameters,
                                                                   symbolTable,
                                                                   identifier.getPosition()),
                                     parameters,
                                     identifier.getPosition());
    }

    /**
     * Checks whether the next token is a Keyword token that represents an integer and returns the
     * type if this is the case. If the token is not a Keyword token, null is returned.
     *
     * @return the type of a KeywordToken
     */
    @Nullable Type parseType() {
        final Token sym = scanner.getSym();
        if (sym instanceof KeywordToken && ((KeywordToken) sym).getKeyword() == Keyword.INT) {
            scanner.processToken();
            return Type.INT;
        }
        return null;
    }

    /**
     * Tries to parse an assignment statement according to the
     * grammar: assignment = ident "=" expression ";".
     * It then returns an AssignmentNode that represents this assignment statement.
     * Returns an error node if a syntax error occurs.
     *
     * @return AssignmentNode representing this assignment statement or ErrorNode
     */
    @NotNull AbstractSyntaxTree parseAssignment() {
        final IdentNode identifier = parseIdentifier();
        if (identifier == null) {
            return new ErrorNode(generateErrorMessage("identifier"));
        }
        final VariableDescription var =
                (VariableDescription) symbolTable.find(identifier.getIdentifier(), (Type[]) null);
        if (var == null) {
            return new ErrorNode(String.format("Reference to unknown variable %s", identifier));
        }
        return new AssignmentNode(new VariableAccessNode(var, identifier.getPosition()),
                                  parseAssignmentWithoutIdent());
    }

    /**
     * This method is a helper for parsing assignments that assumes that the token stream was
     * already checked for an identifier.
     * It is needed to distinguish assignments and intern procedure calls who both start with
     * an IDENT.
     *
     * @return the expression that will be assigned to a variable
     */
    @NotNull
    private Expression parseAssignmentWithoutIdent() {
        Token sym = scanner.getSym();
        if (sym.getType() != ASSIGN) {
            return new ErrorNode(generateErrorMessage("'='"));
        }
        scanner.processToken();

        final Expression expression = parseExpression();
        if (expression instanceof ErrorNode) {
            return expression;
        }
        sym = scanner.getSym();
        if (sym.getType() != SEMICOLON) {
            return new ErrorNode(generateErrorMessage("';'"));
        }
        scanner.processToken();

        return expression;
    }

    /**
     * Tries to parse an if statement according to the
     * grammar: if_statement  = "if" "(" expression ")" "{" statement_sequence "}" "else" "{"statement_sequence "}".
     * It then returns an IfElseNode that represents this if statement.
     * Returns an error node if a syntax error occurs.
     *
     * @return IfElseNode representing this if statement or ErrorNode
     */
    @NotNull Statement parseIfStatement() {
        // if keyword
        Token sym = scanner.getSym();
        if (!(sym instanceof KeywordToken && ((KeywordToken) sym).getKeyword() == Keyword.IF)) {
            return new ErrorNode(generateErrorMessage("'if' keyword"));
        }
        scanner.processToken();

        final Position positionOfIfKeyword = sym.getPosition();

        sym = scanner.getSym();
        if (sym.getType() != L_PAREN) {
            return new ErrorNode(generateErrorMessage("'('"));
        }
        scanner.processToken();

        // condition
        final Expression condition = parseExpression();
        if (condition instanceof ErrorNode) {
            return (ErrorNode) condition;
        }
        if (scanner.getSym().getType() != R_PAREN) {
            return new ErrorNode(generateErrorMessage("')'"));
        }
        scanner.processToken();

        // if-associated block:
        if (scanner.getSym().getType() != L_BRACE) {
            return new ErrorNode(generateErrorMessage("'{'"));
        }
        scanner.processToken();

        final Statement ifBranch = parseStatementSequence();
        if (ifBranch instanceof ErrorNode) {
            return new ErrorNode(generateErrorMessage("statement(s)"));
            //return ifBranch;
        }
        if (scanner.getSym().getType() != R_BRACE) {
            return new ErrorNode(generateErrorMessage("'}'"));
        }
        scanner.processToken();

        sym = scanner.getSym();
        if (!(sym instanceof KeywordToken
              && ((KeywordToken) scanner.getSym()).getKeyword() == Keyword.ELSE)) {
            return new ErrorNode(generateErrorMessage("'else' keyword"));
        }
        scanner.processToken();

        // else-associated block
        if (scanner.getSym().getType() != L_BRACE) {
            return new ErrorNode(generateErrorMessage("'{'"));
        }
        scanner.processToken();

        final Statement elseBranch = parseStatementSequence();
        if (elseBranch instanceof ErrorNode) {
            return new ErrorNode(generateErrorMessage("statement(s)"));
        }
        if (scanner.getSym().getType() != R_BRACE) {
            return new ErrorNode(generateErrorMessage("'}'"));
        }
        scanner.processToken();

        final IfNode ifNode = new IfNode(condition, ifBranch, positionOfIfKeyword);
        return new IfElseNode(ifNode, elseBranch);
    }

    /**
     * Tries to parse a while statement according to the
     * grammar: while_statement = "while" "(" expression ")" "{" statement_sequence "}".
     * It then returns a WhileNode that represents this while statement.
     * Returns an error node if a syntax error occurs.
     *
     * @return WhileNode representing this while statement or ErrorNode
     */
    @NotNull Statement parseWhileStatement() {
        Token sym = scanner.getSym();

        // while keyword
        if (!(sym instanceof KeywordToken && ((KeywordToken) sym).getKeyword() == Keyword.WHILE)) {
            return new ErrorNode(generateErrorMessage("'while' keyword"));
        }
        scanner.processToken();
        final Position position = sym.getPosition();

        sym = scanner.getSym();
        if (sym.getType() != L_PAREN) {
            return new ErrorNode(generateErrorMessage("'('"));
        }
        scanner.processToken();

        // condition
        final Expression condition = parseExpression();
        if (condition instanceof ErrorNode) {
            return (ErrorNode) condition;
        }
        sym = scanner.getSym();
        if (sym.getType() != R_PAREN) {
            return new ErrorNode(generateErrorMessage("')'"));
        }
        scanner.processToken();

        sym = scanner.getSym();
        if (sym.getType() != L_BRACE) {
            return new ErrorNode(generateErrorMessage("'{'"));
        }
        scanner.processToken();

        // associated block
        final Statement statements = parseStatementSequence();
        if (statements instanceof ErrorNode) {
            return new ErrorNode(generateErrorMessage("statement(s)"));
        }
        if (scanner.getSym().getType() != R_BRACE) {
            return new ErrorNode(generateErrorMessage("'}'"));
        }
        scanner.processToken();

        return new WhileNode(condition, statements, position);
    }

    /**
     * Tries to parse a return statement according to the
     * grammar: return_statement = "return" [ simple_expression ] ";".
     * It then returns a ReturnNode.
     * Returns an error node if a syntax error occurs.
     *
     * @return ReturnNode representing this return statement or ErrorNode
     */
    @NotNull Statement parseReturnStatement() {
        Token sym = scanner.getSym();
        if (!(sym instanceof KeywordToken && ((KeywordToken) sym).getKeyword() == Keyword.RETURN)) {
            return new ErrorNode(generateErrorMessage("'return' keyword"));
        }
        scanner.processToken();
        final Position position = sym.getPosition();

        if (scanner.getSym().getType() == SEMICOLON) {
            // there is no simple expression in between
            scanner.processToken();
            return new ReturnNode(position);
        }

        final Expression expression = parseSimpleExpression();
        if (expression instanceof ErrorNode) {
            return (ErrorNode) expression;
        }

        // before doing something with the expression the terminal semicolon must be validated
        sym = scanner.getSym();
        if (sym.getType() != SEMICOLON) {
            return new ErrorNode(generateErrorMessage("';'"));
        }
        scanner.processToken();

        return new ReturnNode(expression, position);
    }

    /**
     * Tries to parse actual parameters according to the
     * grammar: actual_parameters = "(" [expression {"," expression}] ")".
     * It then returns the list of trees that represent the expressions used as parameters.
     * Returns null if a syntax error occurs.
     *
     * @return list of actual parameters
     */
    @Nullable ParameterListNode parseActualParameters() {
        if (scanner.getSym().getType() != L_PAREN) {
            return null; // TODO return an error node later on
        }
        scanner.processToken();

        final List<Expression> paramList = new LinkedList<>();

        Expression node = parseExpression();
        // it is okay if no expression comes here
        // but it is still necessary to check for the right paren afterwards
        if (!(node instanceof ErrorNode)) {
            paramList.add(node);

            while (scanner.getSym().getType() == COMMA) {
                scanner.processToken();

                node = parseExpression();
                if (node instanceof ErrorNode) {
                    return null; // TODO return the error node
                }
                paramList.add(node);

            }
        }

        if (scanner.getSym().getType() != R_PAREN) {
            return null; // TODO return an error node later on
        }
        scanner.processToken();
        return new ParameterListNode(paramList);
    }

    /**
     * Tries to parse an expression according to the
     * grammar: expression = simple_expression [("==" | "<" | "<=" | ">" | ">=") simple_expression].
     * This method then returns the subtree representing this expression.
     *
     * @return syntax tree for this expression
     */
    @NotNull Expression parseExpression() {
        Expression node = parseSimpleExpression();
        final Token sym = scanner.getSym();
        switch (sym.getType()) {
            case EQUAL:
                scanner.processToken();
                node = new BinaryOperationNode(node,
                                               BinaryOperationNodeType.EQUAL,
                                               parseSimpleExpression());
                break;
            case LOWER:
                scanner.processToken();
                node = new BinaryOperationNode(node,
                                               BinaryOperationNodeType.LOWER,
                                               parseSimpleExpression());
                break;
            case LOWER_EQUAL:
                scanner.processToken();
                node = new BinaryOperationNode(node,
                                               BinaryOperationNodeType.LOWER_EQUAL,
                                               parseSimpleExpression());
                break;
            case GREATER:
                scanner.processToken();
                node = new BinaryOperationNode(node,
                                               BinaryOperationNodeType.GREATER,
                                               parseSimpleExpression());
                break;
            case GREATER_EQUAL:
                scanner.processToken();
                node = new BinaryOperationNode(node,
                                               BinaryOperationNodeType.GREATER_EQUAL,
                                               parseSimpleExpression());
                break;
        }

        return node;
    }

    /**
     * Tries to parse a simple expression according to the
     * grammar: simple_expression = term {("+" | "-"  ) term}.
     * This method then returns the subtree representing this simple expression
     *
     * @return syntax tree for this simple expression
     */
    @NotNull Expression parseSimpleExpression() {
        Expression node = parseTerm();
        while (!(node instanceof ErrorNode)) {
            final Token sym = scanner.getSym();
            if (sym.getType() == PLUS) {
                scanner.processToken();
                node = new BinaryOperationNode(node, BinaryOperationNodeType.PLUS, parseTerm());
            } else if (sym.getType() == MINUS) {
                scanner.processToken();
                node = new BinaryOperationNode(node, BinaryOperationNodeType.MINUS, parseTerm());
            } else {
                // this means that at least one term could be found which is valid for this grammar
                break;
            }
        }
        return node;
    }

    /**
     * Tries to parse a term according to the
     * grammar: term = factor {("*" | "/" ) factor}.
     * This method then returns the AST for this term.
     *
     * @return syntax tree for this term
     */
    @NotNull Expression parseTerm() {
        Expression node = parseFactor();
        while (!(node instanceof ErrorNode)) {
            final Token sym = scanner.getSym();
            if (sym.getType() == TIMES) {
                scanner.processToken();
                node = new BinaryOperationNode(node, BinaryOperationNodeType.TIMES, parseFactor());
            } else if (sym.getType() == DIVIDE) {
                scanner.processToken();
                node = new BinaryOperationNode(node, BinaryOperationNodeType.DIVIDE, parseFactor());
            } else {
                // at least one factor was found which is valid for this grammar
                break;
            }
        }
        return node;
    }

    /**
     * Tries to parse a factor according to the
     * grammar: factor = ident | intern_procedure_call | number | "(" expression ")".
     * This method then returns the syntax tree for this factor.
     *
     * @return syntax tree for this factor
     */
    @NotNull Expression parseFactor() {
        final IdentNode identifier = parseIdentifier();
        if (identifier != null) {
            final ParameterListNode parameters = parseActualParameters();

            /*
             * Parse intern procedure call
             */
            if (parameters != null) {
                // Finds the procedure lazily after the whole file was parsed.
                // This avoids evaluating the tree `parameters` multiple times and directly here.
                return new ProcedureCallNode(new ProcedureDescriptionProxy(identifier.getIdentifier(),
                                                                           parameters,
                                                                           symbolTable,
                                                                           identifier.getPosition()),
                                             parameters,
                                             identifier.getPosition());
            }

            /*
             * Parse a variable access
             */
            final VariableDescription var =
                    (VariableDescription) symbolTable.find(identifier.getIdentifier(),
                                                           (Type[]) null);

            if (var == null) {
                return new ErrorNode(String.format("Reference to unknown variable %s", identifier));
            }
            return new VariableAccessNode(var, identifier.getPosition());
        }

        /*
         * Parse a number
         */
        Expression node = parseNumber();
        if (!(node instanceof ErrorNode)) {
            return node;
        }

        /*
         * Parse an expression
         */
        if (scanner.getSym().getType() != L_PAREN) {
            return new ErrorNode(generateErrorMessage("'('"));
        }
        scanner.processToken();

        node = parseExpression();
        if (scanner.getSym().getType() != R_PAREN) {
            return new ErrorNode(generateErrorMessage("')'"));
        }
        scanner.processToken();

        return node; // whether the above parseExpression() was successful
    }

    /**
     * Checks the underlying token iterator for a single number.
     *
     * @return whether a single NUMBER token comes next
     */
    @NotNull Expression parseNumber() {
        final Token sym = scanner.getSym();
        final Position pos = sym.getPosition();
        if (sym.getType() == NUMBER) {
            ConstantNode<Long> node;
            if (sym instanceof NumberToken) {
                node = new ConstantNode<>(Type.INT, ((NumberToken) sym).getNumber(), pos);
            } else {
                node = new ConstantNode<>(Type.INT, scanner.getNum(), pos);
            }
            scanner.processToken();
            return node;
        } else {
            return new ErrorNode(generateErrorMessage("number literal"));
        }
    }


    /**
     * Checks whether the next token is an IDENT token and returns the identifier if this is the
     * case. If the token is not an IDENT token, null is returned.
     *
     * @return an identifier of a single IDENT token that comes next
     */
    @Nullable IdentNode parseIdentifier() {
        // this method does not return a Node yet as it does not know enough context
        // this could be a declaration, a variable or a procedure, for instance
        final Token sym = scanner.getSym();
        if (sym.getType() == IDENT) {
            String identifier;
            if (sym instanceof IdentToken) {
                identifier = ((IdentToken) sym).getIdent();
            } else {
                identifier = scanner.getId();
            }
            scanner.processToken();
            return new IdentNode(identifier, sym.getPosition());
        } else {
            return null;
        }
    }

    /**
     * Creates an error message with the expected symbol. The actual value is derived from the
     * current token of the scanner.
     *
     * @param expectedConstruct a String that describes what should have been there
     * @return a string that can be used as an output for users
     */
    @NotNull
    private String generateErrorMessage(@NotNull final String expectedConstruct) {
        final String template = "%s was found, but %s was expected.";
        return String.format(template, scanner.getSym(), expectedConstruct);
    }
}
