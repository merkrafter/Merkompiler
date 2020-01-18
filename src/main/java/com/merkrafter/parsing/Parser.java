package com.merkrafter.parsing;

import com.merkrafter.lexing.*;
import com.merkrafter.representation.*;
import com.merkrafter.representation.ast.*;

import java.util.Arrays;
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
    private final Scanner scanner;

    /**
     * The base symbol table that encloses all others.
     */
    private final SymbolTable symbolTable;


    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new Parser based on a scanner that provides tokens.
     ***************************************************************/
    public Parser(final Scanner scanner) {
        this(scanner, null);
    }

    /**
     * Creates a new Parser with a set of global variables. Can be used for testing purposes.
     */
    Parser(final Scanner scanner, final SymbolTable globalVariables) {
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
     */
    public boolean parse() {
        return parseClass() && scanner.getSym().getType() == EOF;
    }

    boolean parseClass() {
        if (scanner.getSym() instanceof KeywordToken
            && ((KeywordToken) scanner.getSym()).getKeyword() == Keyword.CLASS) {
            scanner.processToken();
            if (parseIdentifier() != null) {
                if (parseClassBody()) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean parseClassBody() {
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

    boolean parseDeclarations() {
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
    private boolean parseFinalDeclaration() {
        final Token sym = scanner.getSym();
        if (!(sym instanceof KeywordToken && ((KeywordToken) sym).getKeyword() == Keyword.FINAL)) {
            return false;
        }
        scanner.processToken();

        final Type type = parseType();
        if (type == null) {
            return false;
        }
        final String identifier = parseIdentifier();
        if (identifier == null) {
            return false;
        }
        if (scanner.getSym().getType() != ASSIGN) {
            return false;
        }
        scanner.processToken();
        if (parseExpression() == null) {
            return false;
        }
        if (scanner.getSym().getType() != SEMICOLON) {
            return false;
        }

        // TODO evaluate the expression to set the value correctly
        final VariableDescription var = new VariableDescription(identifier, type, null, true);
        // TODO check whether this is successful
        symbolTable.insert(var);

        scanner.processToken();
        return true;
    }

    boolean parseMethodDeclaration() {
        if (parseMethodHead()) {
            if (parseMethodBody()) {
                return true;
            }
        }
        return false;
    }

    boolean parseMethodHead() {
        if (scanner.getSym() instanceof KeywordToken
            && ((KeywordToken) scanner.getSym()).getKeyword() == Keyword.PUBLIC) {
            scanner.processToken();
            if (parseMethodType() != null) {
                if (parseIdentifier() != null) {
                    return parseFormalParameters();
                }
            }
        }
        return false;
    }

    /**
     * Tries to parse a method type. Methods in JavaSST can only return INT or VOID.
     *
     * @return type of a method or null if no applicable type was found
     */
    Type parseMethodType() {
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

    boolean parseFormalParameters() {
        if (scanner.getSym().getType() == L_PAREN) {
            scanner.processToken();
            if (parseFpSection()) {
                while (scanner.getSym().getType() == COMMA) {
                    scanner.processToken();
                    if (!parseFpSection()) {
                        return false;
                    }
                }
            }
            if (scanner.getSym().getType() == R_PAREN) {
                scanner.processToken();
                return true;
            }

        }
        return false;
    }

    boolean parseFpSection() {
        if (parseType() != null) {
            return parseIdentifier() != null; // already reads the next token
        }
        return false;
    }

    boolean parseMethodBody() {
        if (scanner.getSym().getType() == L_BRACE) {
            scanner.processToken();
            while (parseLocalDeclaration()) ; // only iterate through them for now
            if (!(parseStatementSequence() instanceof ErrorNode)) {
                if (scanner.getSym().getType() == R_BRACE) {
                    scanner.processToken();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Tries to parse a local declaration and returns whether the next 3 tokens match the
     * grammar: local_declaration = type ident ";".
     * <p>
     * If this succeeds, the variable is pushed into the symbol table.
     *
     * @return whether the next tokens represent a local declaration
     */
    boolean parseLocalDeclaration() {
        final Type type = parseType();
        if (type == null) {
            return false;
        }
        final String identifier = parseIdentifier();
        if (identifier == null) {
            return false;
        }
        if (scanner.getSym().getType() != SEMICOLON) { // no need to store this in a variable
            return false;
        }
        scanner.processToken();

        // FIXME this line assumes that only int values exist and therefore sets the value to 0
        // if more types come into play, a map of default values should be maintained somewhere
        final VariableDescription var = new VariableDescription(identifier, type, 0, false);
        // TODO detect multi-declarations as a part of the semantics analysis
        symbolTable.insert(var);

        return true;
    }

    ASTBaseNode parseStatementSequence() {
        final ASTBaseNode headNode = parseStatement();
        if (headNode instanceof ErrorNode) {
            return headNode;
        }
        ASTBaseNode last = headNode;
        ASTBaseNode current = parseStatement();
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
    ASTBaseNode parseStatement() {
        // factoring of
        // statement = ident '=' expression ';' | ident actual_parameters ';'
        //             ^ assignment               ^ procedure call
        ASTBaseNode node = parseStatementForAssignmentOrProcedureCall();
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
        return new ErrorNode("Expected statement but found " + scanner.getSym().getType());
    }

    /**
     * This method is a helper for differentiating between assignments or procedure calls.
     * It is needed because both start with an IDENT token.
     *
     * @return AssignmentNode, ProcedureCallNode, or ErrorNode
     */
    private ASTBaseNode parseStatementForAssignmentOrProcedureCall() {
        final String identifier = parseIdentifier();
        if (identifier == null) {
            // both an assignment and a procedure call need an identifier first
            return new ErrorNode("Expected an identifier");
        }

        // try parsing an assignment
        final ASTBaseNode expression = parseAssignmentWithoutIdent();
        if (!(expression instanceof ErrorNode)) {
            final VariableDescription var = (VariableDescription) symbolTable.find(identifier);
            final VariableAccessNode varNode = new VariableAccessNode(var);
            return new AssignmentNode(varNode, expression);
        }

        // begin parsing a procedure call
        final ParameterListNode parameters = parseActualParameters();
        if (parameters == null) {
            return new ErrorNode("Expected a parameter list");
        }
        final Token sym = scanner.getSym();
        if (sym.getType() != SEMICOLON) {
            return new ErrorNode("Expected ';' but found " + sym.getType());
        }
        // this actually is a procedure call
        scanner.processToken();

        return new ProcedureCallNode(new ProcedureDescriptionProxy(identifier,
                                                                   parameters,
                                                                   symbolTable), parameters);
    }

    /**
     * Checks whether the next token is a Keyword token that represents an integer and returns the
     * type if this is the case. If the token is not a Keyword token, null is returned.
     *
     * @return the type of a KeywordToken
     */
    Type parseType() {
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
    ASTBaseNode parseAssignment() {
        final String identifier = parseIdentifier();
        if (identifier == null) {
            return new ErrorNode("");
        }
        final VariableDescription var = (VariableDescription) symbolTable.find(identifier);
        return new AssignmentNode(new VariableAccessNode(var), parseAssignmentWithoutIdent());
    }

    /**
     * This method is a helper for parsing assignments that assumes that the token stream was
     * already checked for an identifier.
     * It is needed to distinguish assignments and intern procedure calls who both start with
     * an IDENT.
     *
     * @return the expression that will be assigned to a variable
     */
    private ASTBaseNode parseAssignmentWithoutIdent() {
        Token sym = scanner.getSym();
        if (sym.getType() != ASSIGN) {
            return new ErrorNode("Expected '=' but found " + sym.getType());
        }
        scanner.processToken();

        final ASTBaseNode expression = parseExpression();
        if (expression instanceof ErrorNode) {
            return expression;
        }
        sym = scanner.getSym();
        if (sym.getType() != SEMICOLON) {
            return new ErrorNode("Expected ';' but found " + sym.getType());
        }
        scanner.processToken();

        return expression;
    }

    boolean parseProcedureCall() {
        if (parseInternProcedureCall() && scanner.getSym().getType() == SEMICOLON) {
            scanner.processToken();
            return true;
        }
        return false;
    }

    boolean parseInternProcedureCall() {
        if (parseIdentifier() != null) {
            return parseActualParameters() != null;
        }
        return false;
    }

    /**
     * Tries to parse an if statement according to the
     * grammar: if_statement  = "if" "(" expression ")" "{" statement_sequence "}" "else" "{"statement_sequence "}".
     * It then returns an IfElseNode that represents this if statement.
     * Returns an error node if a syntax error occurs.
     *
     * @return IfElseNode representing this if statement or ErrorNode
     */
    ASTBaseNode parseIfStatement() {
        // if keyword
        Token sym = scanner.getSym();
        if (!(sym instanceof KeywordToken && ((KeywordToken) sym).getKeyword() == Keyword.IF)) {
            return new ErrorNode("Expected if keyword but found " + sym.getType());
        }
        scanner.processToken();

        sym = scanner.getSym();
        if (sym.getType() != L_PAREN) {
            return new ErrorNode("Expected '(' but found " + sym.getType());
        }
        scanner.processToken();

        // condition
        final ASTBaseNode condition = parseExpression();
        if (condition instanceof ErrorNode) {
            return condition;
        }
        if (scanner.getSym().getType() != R_PAREN) {
            return new ErrorNode("Expected ')' but found " + sym.getType());
        }
        scanner.processToken();

        // if-associated block:
        if (scanner.getSym().getType() != L_BRACE) {
            return new ErrorNode("Expected '{' but found " + sym.getType());
        }
        scanner.processToken();

        final ASTBaseNode ifBranch = parseStatementSequence();
        if (ifBranch instanceof ErrorNode) {
            return new ErrorNode("Expected statement(s), but found " + scanner.getSym().getType());
            //return ifBranch;
        }
        if (scanner.getSym().getType() != R_BRACE) {
            return new ErrorNode("Expected '}' but found " + sym.getType());
        }
        scanner.processToken();

        sym = scanner.getSym();
        if (!(sym instanceof KeywordToken
              && ((KeywordToken) scanner.getSym()).getKeyword() == Keyword.ELSE)) {
            return new ErrorNode("Expected else keyword but found " + sym.getType());
        }
        scanner.processToken();

        // else-associated block
        if (scanner.getSym().getType() != L_BRACE) {
            return new ErrorNode("Expected '{' but found " + sym.getType());
        }
        scanner.processToken();

        final ASTBaseNode elseBranch = parseStatementSequence();
        if (elseBranch instanceof ErrorNode) {
            return new ErrorNode("Expected statement(s), but found " + scanner.getSym().getType());
            //return elseBranch;
        }
        if (scanner.getSym().getType() != R_BRACE) {
            return new ErrorNode("Expected '}' but found " + sym.getType());
        }
        scanner.processToken();

        final IfNode ifNode = new IfNode(condition, ifBranch);
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
    ASTBaseNode parseWhileStatement() {
        Token sym = scanner.getSym();

        // while keyword
        if (!(sym instanceof KeywordToken && ((KeywordToken) sym).getKeyword() == Keyword.WHILE)) {
            return new ErrorNode("Expected while keyword but found " + sym.getType());
        }
        scanner.processToken();

        sym = scanner.getSym();
        if (sym.getType() != L_PAREN) {
            return new ErrorNode("Expected '(' but found " + sym.getType());
        }
        scanner.processToken();

        // condition
        final ASTBaseNode condition = parseExpression();
        if (condition instanceof ErrorNode) {
            return condition;
        }
        sym = scanner.getSym();
        if (sym.getType() != R_PAREN) {
            return new ErrorNode("Expected ')' but found " + sym.getType());
        }
        scanner.processToken();

        sym = scanner.getSym();
        if (sym.getType() != L_BRACE) {
            return new ErrorNode("Expected '{' but found " + sym.getType());
        }
        scanner.processToken();

        // associated block
        final ASTBaseNode statements = parseStatementSequence();
        if (statements instanceof ErrorNode) {
            return new ErrorNode("Expected statement(s), but found " + scanner.getSym().getType());
        }
        if (scanner.getSym().getType() != R_BRACE) {
            return new ErrorNode("Expected '}' but found " + sym.getType());
        }
        scanner.processToken();

        return new WhileNode(condition, statements);
    }

    /**
     * Tries to parse a return statement according to the
     * grammar: return_statement = "return" [ simple_expression ] ";".
     * It then returns a ReturnNode.
     * Returns an error node if a syntax error occurs.
     *
     * @return ReturnNode representing this return statement or ErrorNode
     */
    ASTBaseNode parseReturnStatement() {
        Token sym = scanner.getSym();
        if (!(sym instanceof KeywordToken && ((KeywordToken) sym).getKeyword() == Keyword.RETURN)) {
            return new ErrorNode("Expected return keyword but found " + sym.getType());
        }
        scanner.processToken();

        // TODO is this extra if branch even needed?
        if (scanner.getSym().getType() == SEMICOLON) {
            // there is no simple expression in between
            scanner.processToken();
            return new ReturnNode();
        }

        final ASTBaseNode expression = parseSimpleExpression();
        if (expression instanceof ErrorNode) {
            return expression;
        }

        // before doing something with the expression the terminal semicolon must be validated
        sym = scanner.getSym();
        if (sym.getType() != SEMICOLON) {
            return new ErrorNode("Expected semicolon but found " + sym.getType());
        }
        scanner.processToken();

        return new ReturnNode(expression);
    }

    /**
     * Tries to parse actual parameters according to the
     * grammar: actual_parameters = "(" [expression {"," expression}] ")".
     * It then returns the list of trees that represent the expressions used as parameters.
     * Returns null if a syntax error occurs.
     *
     * @return list of actual parameters
     */
    ParameterListNode parseActualParameters() {
        if (scanner.getSym().getType() != L_PAREN) {
            return null; // TODO return an error node later on
        }
        scanner.processToken();

        final List<ASTBaseNode> paramList = new LinkedList<>();

        ASTBaseNode node = parseExpression();
        // it is okay if no expression comes here
        // but it is still necessary to check for the right paren afterwards
        if (node != null && !(node instanceof ErrorNode)) {
            paramList.add(node);

            while (scanner.getSym().getType() == COMMA) {
                scanner.processToken();

                node = parseExpression();
                if (node == null || node instanceof ErrorNode) {
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
    ASTBaseNode parseExpression() {
        ASTBaseNode node = parseSimpleExpression();
        if (node == null) { // TODO check whether this case can happen; better avoid it
            return null;
        }
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
    ASTBaseNode parseSimpleExpression() {
        ASTBaseNode node = parseTerm();
        while (node != null && !(node instanceof ErrorNode)) {
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
    ASTBaseNode parseTerm() {
        ASTBaseNode node = parseFactor();
        while (node != null && !(node instanceof ErrorNode)) {
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
    ASTBaseNode parseFactor() {
        final String identifier = parseIdentifier();
        if (identifier != null) {
            final ParameterListNode parameters = parseActualParameters();

            /*
             * Parse intern procedure call
             */
            if (parameters != null) {
                // Finds the procedure lazily after the whole file was parsed.
                // This avoids evaluating the tree `parameters` multiple times and directly here.
                return new ProcedureCallNode(new ProcedureDescriptionProxy(identifier,
                                                                           parameters,
                                                                           symbolTable),
                                             parameters);
            }

            /*
             * Parse a variable access
             */
            final VariableDescription var = (VariableDescription) symbolTable.find(identifier);

            // no need to check whether a variable was found since the variable access node will
            // communicate an error by itself when its hasSemanticsError() method is called
            return new VariableAccessNode(var);
        }

        /*
         * Parse a number
         */
        ASTBaseNode node = parseNumber();
        if (!(node instanceof ErrorNode)) {
            return node;
        }

        /*
         * Parse an expression
         */
        if (scanner.getSym().getType() != L_PAREN) {
            return new ErrorNode("Expected '(' but found " + scanner.getSym().getType());
        }
        scanner.processToken();

        node = parseExpression();
        if (scanner.getSym().getType() != R_PAREN) {
            return new ErrorNode("Expected ')' but found " + scanner.getSym().getType());
        }
        scanner.processToken();

        return node; // whether the above parseExpression() was successful
    }

    /**
     * Checks the underlying token iterator for a single number.
     *
     * @return whether a single NUMBER token comes next
     */
    ASTBaseNode parseNumber() {
        final Token sym = scanner.getSym();
        if (sym.getType() == NUMBER) {
            ConstantNode<Long> node;
            if (sym instanceof NumberToken) {
                node = new ConstantNode<>(Type.INT, ((NumberToken) sym).getNumber());
            } else {
                node = new ConstantNode<>(Type.INT, scanner.getNum());
            }
            scanner.processToken();
            return node;
        } else {
            return new ErrorNode(String.format(
                    "Expected number literal in %s at (%d, %d) but found %s instead",
                    sym.getFilename(),
                    sym.getLine(),
                    sym.getPosition(),
                    sym.getType().toString()));
        }
    }


    /**
     * Checks whether the next token is an IDENT token and returns the identifier if this is the
     * case. If the token is not an IDENT token, null is returned.
     *
     * @return an identifier of a single IDENT token that comes next
     */
    String parseIdentifier() {
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
            return identifier;
        } else {
            return null;
        }
    }
}
