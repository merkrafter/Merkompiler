package com.merkrafter.lexing;

import org.junit.jupiter.api.BeforeEach;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.merkrafter.lexing.TokenType.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * This class tests the getSym() method of the Scanner class with multiple different input strings.
 *
 * @author merkrafter
 */
class ScannerTest {

    /**
     * This field enables iterating over Strings.
     */
    private StringIterator stringIterator;
    /**
     * This field is the test subject.
     */
    private Scanner scanner;

    /**
     * Sets up an empty stringIterator and initializes the scanner with it.
     */
    @BeforeEach
    void setUp() {
        stringIterator = new StringIterator();
        scanner = new Scanner(stringIterator);
    }

    /**
     * The scanner should be able to handle an empty string by returning the EOF token.
     */
    @org.junit.jupiter.api.Test
    void scanEmptyString() {
        final String programCode = "";
        final TokenType[] expectedTokenList = {EOF};
        shouldScan(programCode, expectedTokenList);
    }

    /**
     * The scanner should be able to handle a string containing just a single identifier.
     */
    @org.junit.jupiter.api.Test
    void scanSingleIdentifier() {
        final String programCode = "identifier";
        final TokenType[] expectedTokenList = {IDENT, EOF};
        shouldScan(programCode, expectedTokenList);
    }

    /**
     * The scanner should be able to handle an assignment operation where the right side is a
     * complex calculation including all four basic arithmetic operations as well as parentheses.
     */
    @org.junit.jupiter.api.Test
    void scanAssignment() {
        final String programCode = "int result = a+(b-c)*d/e;";
        final TokenType[] expectedTokenList = {IDENT, IDENT, ASSIGN, IDENT, PLUS, L_PAREN, IDENT, MINUS, IDENT, R_PAREN, TIMES, IDENT, DIVIDE, IDENT, SEMICOLON, EOF};
        shouldScan(programCode, expectedTokenList);
    }

    /**
     * The scanner should be able to handle a basic main function including an array as arguments.
     * This test includes the detection of square brackets and braces.
     */
    @org.junit.jupiter.api.Test
    void scanMainFunction() {
        final String programCode = "public static void main(String[] args) {}";
        final TokenType[] expectedTokenList = {IDENT, IDENT, IDENT, IDENT, L_PAREN, IDENT, L_SQ_BRACKET, R_SQ_BRACKET, IDENT, R_PAREN, L_BRACE, R_BRACE, EOF};
        shouldScan(programCode, expectedTokenList);
    }

    /**
     * The scanner should be able to handle an empty class that has a privacy modifier, the class
     * keyword, a name and braces.
     */
    @org.junit.jupiter.api.Test
    void scanEmptyClass() {
        final String programCode = "public class Test{}";
        final TokenType[] expectedTokenList = {IDENT, IDENT, IDENT, L_BRACE, R_BRACE, EOF};
        shouldScan(programCode, expectedTokenList);
    }

    /**
     * Collects all tokens emitted by this scanner.
     *
     * @param scanner the object to get the tokens from
     * @return a list of all tokens found
     */
    private List<TokenType> getTokenList(final Scanner scanner) {
        LinkedList<TokenType> tokenList = new LinkedList<>();
        do {
            scanner.getSym();
            tokenList.add(scanner.sym);
        } while (scanner.sym != TokenType.EOF);
        return tokenList;
    }

    /**
     * Conveniently wraps the assertions of equal tokens between expected and the ones emitted by a scanner.
     * It therefore sets the programCode as an input to this class's stringIterator and reads from
     * its scanner. Both must be initialized before this method can be called safely.
     *
     * @param programCode the string to tokenize by this class's scanner
     * @param expectedTokenList an array with all expected tokens in the right order
     */
    private void shouldScan(final String programCode, final TokenType[] expectedTokenList) {
        stringIterator.setString(programCode);
        final List<TokenType> actualTokenList = getTokenList(scanner);
        assertArrayEquals(expectedTokenList, actualTokenList.toArray(), actualTokenList.toString());
    }

    /**
     * This class provides the possibility to iterate over strings.
     * It can be used as a tool to mock a file as the input to a Scanner.
     */
    private class StringIterator implements Iterator<Character> {
        /**
         * The string to iterate over. It is not set by the constructor as the input string will not
         * be known during the setUp method anyway.
         */
        private String string = "";
        /**
         * Tracks the index in the string.
         */
        private int index = 0;

        void setString(String string) {
            this.string = string;
        }

        /**
         * @return true if there are more characters in the string
         */
        @Override
        public boolean hasNext() {
            return index < string.length();
        }

        /**
         * Returns the next character in the string if there is any left. This should be checked via
         * hasNext() before this method is called in order to avoid an exception.
         *
         * @return next character in the string
         *
         * @throws IndexOutOfBoundsException if there are no more characters left to read
         */
        @Override
        public Character next() {
            return string.charAt(index++);
        }
    }
}