package com.merkrafter.parsing;

import com.merkrafter.lexing.Keyword;
import com.merkrafter.lexing.KeywordToken;
import com.merkrafter.lexing.Scanner;

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

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new Parser based on a scanner that provides tokens.
     ***************************************************************/
    public Parser(final Scanner scanner) {
        this.scanner = scanner;
        this.scanner.processToken();
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Parses the tokens given by the underlying token iterator.
     */
    public boolean parse() {
        return false;
    }

    boolean parseMethodHead() {
        if (scanner.getSym() instanceof KeywordToken
            && ((KeywordToken) scanner.getSym()).getKeyword() == Keyword.PUBLIC) {
            scanner.processToken();
            if (parseMethodType()) {
                if (parseIdentifier()) {
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
        if (parseType()) {
            return parseIdentifier(); // already reads the next token
        }
        return false;
    }

    boolean parseLocalDeclaration() {
        if (parseType()) {
            if (parseIdentifier()) {
                if (scanner.getSym().getType() == SEMICOLON) {
                    scanner.processToken();
                    return true;
                }
            }
        }
        return false;
    }

    boolean parseStatementSequence() {
        return false;
    }

    boolean parseStatement() {
        // factoring of
        // statement = ident '=' expression ';' | ident actual_parameters ';'
        //             ^ assignment               ^ procedure call
        if (parseStatementForAssignmentOrProcedureCall()) {
            return true;
        }
        if (parseReturnStatement()) {
            return true;
        }
        return false;
    }

    private boolean parseStatementForAssignmentOrProcedureCall() {
        if (parseIdentifier()) {
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
     * @return whether the current symbol is a KeywordToken and represents an "int"
     */
    boolean parseType() {
        if (scanner.getSym() instanceof KeywordToken
            && ((KeywordToken) scanner.getSym()).getKeyword() == Keyword.INT) {
            scanner.processToken();
            return true;
        }
        return false;
    }

    boolean parseAssignment() {
        if (parseIdentifier()) {
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
        if (parseIdentifier()) {
            return parseActualParameters();
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
                        error("Expected expression after comma in actual parameters");
                        return false;
                    }
                }
            } else {
                return true; // it is okay if no expression comes here
            }
        } else {
            error("Expected left parenthesis in actual parameters");
            return false;
        }
        if (scanner.getSym().getType() == R_PAREN) {
            scanner.processToken();
            return true;
        } else {
            error("Wrong use of parenthesis");
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
            error("Error parsing expression");
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
        if (parseIdentifier()) {
            return true;
        } else if (parseNumber()) {
            return true;
        } else if (scanner.getSym().getType() == L_PAREN) {
            scanner.processToken();
            final boolean success = parseExpression();
            if (scanner.getSym().getType() == R_PAREN) {
                scanner.processToken();
            } else {
                error("Wrong use of parenthesis");
                return false;
            }
            return success; // whether the above parseExpression() was successful
        }
        return parseInternProcedureCall();
    }

    /**
     * Checks the underlying token iterator for a single number.
     *
     * @return whether a single NUMBER token comes next
     */
    boolean parseNumber() {
        if (scanner.getSym().getType() == NUMBER) {
            scanner.processToken();
            return true;
        } else {
            error("Expected number but found " + scanner.getSym().getType().name());
            return false;
        }
    }

    /**
     * Checks the underlying token iterator for a single identifier.
     *
     * @return whether a single IDENT token comes next
     */
    boolean parseIdentifier() {
        if (scanner.getSym().getType() == IDENT) {
            scanner.processToken();
            return true;
        } else {
            error("Expected identifier but found " + scanner.getSym().getType().name());
            return false;
        }
    }

    /**
     * Prints the given message to stderr.
     * Change soon, as only the main class should be printing.
     *
     * @param msg the message to print
     */
    private static void error(final String msg) {
        System.err.println(msg);
    }

}
