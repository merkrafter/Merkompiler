package com.merkrafter.parsing;

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

    boolean parseInternProcedureCall() {
        return false;
    }

    boolean parseActualParameters() {
        return false;
    }

    boolean parseExpression() {
        return false;
    }

    boolean parseSimpleExpression() {
        return false;
    }

    boolean parseTerm() {
        return false;
    }

    boolean parseFactor() {
        return false;
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
