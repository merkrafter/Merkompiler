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
        return false;
    }

    boolean parseProcedureCall() {
        return false;
    }

    boolean parseInternProcedureCall() {
        if (parseIdentifier()) {
            scanner.processToken();
            return parseActualParameters();
        }
        return false;
    }

    boolean parseReturnStatement() {
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
