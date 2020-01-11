package com.merkrafter.parsing;

import com.merkrafter.lexing.*;
import com.merkrafter.representation.SymbolTable;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.VariableDescription;
import com.merkrafter.representation.ast.ASTBaseNode;
import com.merkrafter.representation.ast.ConstantNode;
import com.merkrafter.representation.ast.ErrorNode;

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

    private boolean parseFinalDeclaration() {
        if (scanner.getSym() instanceof KeywordToken
            && ((KeywordToken) scanner.getSym()).getKeyword() == Keyword.FINAL) {
            scanner.processToken();
            if (parseType() != null) {
                if (parseIdentifier() != null) {
                    if (scanner.getSym().getType() == ASSIGN) {
                        scanner.processToken();
                        if (parseExpression()) {
                            if (scanner.getSym().getType() == SEMICOLON) {
                                scanner.processToken();
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
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
            } else if (parseActualParameters() && scanner.getSym().getType() == SEMICOLON) {
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
            if (parseExpression() && scanner.getSym().getType() == SEMICOLON) {
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
            return parseActualParameters();
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
                if (parseExpression()) {
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
                if (parseExpression()) {
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
            } else if (parseSimpleExpression()) {
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

    boolean parseActualParameters() {
        if (scanner.getSym().getType() == L_PAREN) {
            scanner.processToken();
            if (parseExpression()) {
                while (scanner.getSym().getType() == COMMA) {
                    scanner.processToken();
                    if (!parseExpression()) {
                        return false;
                    }
                }
            }
            // it is okay if no expression comes here
            // but it is still necessary to check for the right paren
        } else {
            return false;
        }
        if (scanner.getSym().getType() == R_PAREN) {
            scanner.processToken();
            return true;
        } else {
            return false;
        }
    }

    boolean parseExpression() {
        if (parseSimpleExpression()) {
            switch (scanner.getSym().getType()) {
                case EQUAL:
                case LOWER:
                case LOWER_EQUAL:
                case GREATER:
                case GREATER_EQUAL:
                    scanner.processToken();
                    return parseSimpleExpression();
                default:
                    return true;
            }
        } else {
            return false;
        }
    }

    boolean parseSimpleExpression() {
        boolean success = parseTerm();
        while (success) {
            if (scanner.getSym().getType() == PLUS || scanner.getSym().getType() == MINUS) {
                scanner.processToken();
                success = parseTerm();
            } else {
                break;
            }
        }
        return success;
    }

    boolean parseTerm() {
        boolean success = parseFactor();
        while (success) {
            if (scanner.getSym().getType() == TIMES || scanner.getSym().getType() == DIVIDE) {
                scanner.processToken();
                success = parseFactor();
            } else {
                break;
            }
        }
        return success;
    }

    boolean parseFactor() {
        if (parseIdentifier() != null) {
            // check whether this actually is a intern procedure call
            if (parseActualParameters()) {
                return true;
            }
            return true;
        } else if (!(parseNumber() instanceof ErrorNode)) {
            return true;
        } else if (scanner.getSym().getType() == L_PAREN) {
            scanner.processToken();
            final boolean success = parseExpression();
            if (scanner.getSym().getType() == R_PAREN) {
                scanner.processToken();
            } else {
                return false;
            }
            return success; // whether the above parseExpression() was successful
        }
        return false;
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
