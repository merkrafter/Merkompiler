package com.merkrafter.parsing;

import com.merkrafter.lexing.*;
import com.merkrafter.representation.ProcedureDescription;
import com.merkrafter.representation.SymbolTable;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.VariableDescription;
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
        this.scanner = scanner;
        this.scanner.processToken();
        symbolTable = new SymbolTable(null);
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
            if (parseMethodType()) {
                if (parseIdentifier() != null) {
                    return parseFormalParameters();
                }
            }
        }
        return false;
    }

    boolean parseMethodType() {
        // expecting a keyword
        if (scanner.getSym() instanceof KeywordToken && (
                // check whether this is "void"
                ((KeywordToken) scanner.getSym()).getKeyword() == Keyword.VOID
                //check whether this is "int"
                || ((KeywordToken) scanner.getSym()).getKeyword() == Keyword.INT)) {
            scanner.processToken();
            return true;
        }
        return false;
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
            if (parseStatementSequence()) {
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

        // FIXME this line assumes that only int values exist and therefore sets the value to 0
        // if more types come into play, a map of default values should be maintained somewhere
        final VariableDescription var = new VariableDescription(identifier, type, 0, false);
        // TODO detect multi-declarations as a part of the semantics analysis
        symbolTable.insert(var);

        scanner.processToken();
        return true;
    }

    boolean parseStatementSequence() {
        if (parseStatement()) {
            while (parseStatement()) ; // just read all statements for now; creating AST follows
            return true;
        }
        return false;
    }

    boolean parseStatement() {
        // factoring of
        // statement = ident '=' expression ';' | ident actual_parameters ';'
        //             ^ assignment               ^ procedure call
        if (parseStatementForAssignmentOrProcedureCall()) {
            return true;
        }
        if (parseIfStatement()) {
            return true;
        }
        if (parseWhileStatement()) {
            return true;
        }
        if (parseReturnStatement()) {
            return true;
        }
        return false;
    }

    private boolean parseStatementForAssignmentOrProcedureCall() {
        if (parseIdentifier() != null) {
            if (parseAssignmentWithoutIdent()) {
                return true;
            } else if (parseActualParameters() != null && scanner.getSym().getType() == SEMICOLON) {
                // this actually is a procedure call
                scanner.processToken();
                return true;
            }
        }
        return false;
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

    boolean parseAssignment() {
        if (parseIdentifier() != null) {
            return parseAssignmentWithoutIdent();
        }
        return false;
    }

    /**
     * This method is a helper for parsing assignments that assumes that the token stream was
     * already checked for an identifier.
     * It is needed to distinguish assignments and intern procedure calls who both start with
     * an IDENT.
     *
     * @return whether tokens after an ident match the grammar of assignments
     */
    private boolean parseAssignmentWithoutIdent() {
        if (scanner.getSym().getType() == ASSIGN) {
            scanner.processToken();
            if (!(parseExpression() instanceof ErrorNode)
                && scanner.getSym().getType() == SEMICOLON) {
                scanner.processToken();
                return true;
            }
        }
        return false;
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

    boolean parseIfStatement() {
        if (scanner.getSym() instanceof KeywordToken
            && ((KeywordToken) scanner.getSym()).getKeyword() == Keyword.IF) {
            scanner.processToken();
            // condition:
            if (scanner.getSym().getType() == L_PAREN) {
                scanner.processToken();
                if (!(parseExpression() instanceof ErrorNode)) {
                    if (scanner.getSym().getType() == R_PAREN) {
                        scanner.processToken();
                        // if-associated block:
                        if (scanner.getSym().getType() == L_BRACE) {
                            scanner.processToken();
                            if (parseStatementSequence()) {
                                if (scanner.getSym().getType() == R_BRACE) {
                                    scanner.processToken();
                                    if (scanner.getSym() instanceof KeywordToken
                                        && ((KeywordToken) scanner.getSym()).getKeyword()
                                           == Keyword.ELSE) {
                                        scanner.processToken();
                                        // else-associated block
                                        if (scanner.getSym().getType() == L_BRACE) {
                                            scanner.processToken();
                                            if (parseStatementSequence()) {
                                                if (scanner.getSym().getType() == R_BRACE) {
                                                    scanner.processToken();
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    boolean parseWhileStatement() {
        if (scanner.getSym() instanceof KeywordToken
            && ((KeywordToken) scanner.getSym()).getKeyword() == Keyword.WHILE) {
            scanner.processToken();
            // condition:
            if (scanner.getSym().getType() == L_PAREN) {
                scanner.processToken();
                if (!(parseExpression() instanceof ErrorNode)) {
                    if (scanner.getSym().getType() == R_PAREN) {
                        scanner.processToken();
                        // associated block:
                        if (scanner.getSym().getType() == L_BRACE) {
                            scanner.processToken();
                            if (parseStatementSequence()) {
                                if (scanner.getSym().getType() == R_BRACE) {
                                    scanner.processToken();
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    boolean parseReturnStatement() {
        if (scanner.getSym() instanceof KeywordToken
            && ((KeywordToken) scanner.getSym()).getKeyword() == Keyword.RETURN) {

            scanner.processToken();
            if (scanner.getSym().getType() == SEMICOLON) {
                // there is no simple expression in between
                scanner.processToken();
                return true;
            } else if (!(parseSimpleExpression() instanceof ErrorNode)) {
                if (scanner.getSym().getType() == SEMICOLON) {
                    scanner.processToken();
                    return true;
                } else {
                    return false;
                }
            } else { // neither a semicolon nor a simple expression
                return false;
            }
        }
        return false;
    }

    /**
     * Tries to parse actual parameters according to the
     * grammar: actual_parameters = "(" [expression {"," expression}] ")".
     * It then returns the list of trees that represent the expressions used as parameters.
     * Returns null if a syntax error occurs.
     *
     * @return list of actual parameters
     */
    List<ASTBaseNode> parseActualParameters() {
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
        return paramList;
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
            final List<ASTBaseNode> parameters = parseActualParameters();

            /*
             * Parse intern procedure call
             */
            if (parameters != null) {
                final Type[] typesArray = new Type[parameters.size()];
                // FIXME throws NPE if the one of the parameters is a variable that was not declared
                Arrays.setAll(typesArray, i -> parameters.get(i).getReturnedType());
                final ProcedureDescription procedure =
                        (ProcedureDescription) symbolTable.find(identifier, typesArray);
                // TODO check whether a procedure was found
                // TODO assign parameters to procedure
                return new ProcedureCallNode((ProcedureDescription) symbolTable.find(procedure));
            }

            /*
             * Parse a variable access
             */
            final VariableDescription var = (VariableDescription) symbolTable.find(identifier);

            // TODO check whether a variable was found
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
