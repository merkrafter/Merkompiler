package com.merkrafter.parsing;

import com.merkrafter.lexing.Scanner;
import com.merkrafter.lexing.Token;
import com.merkrafter.lexing.TokenType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    /**
     * The parser should accept a single number.
     */
    @Test
    void parseNumber() {
        final Scanner scanner = new TestScanner(new Token[]{new Token(TokenType.NUMBER, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseNumber());
    }

    /**
     * The parser should not accept an identifier when expecting a number.
     */
    @Test
    void tryParseNoNumber() {
        final Scanner scanner = new TestScanner(new Token[]{new Token(TokenType.IDENT, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertFalse(parser.parseNumber());
    }

    /**
     * The parser should accept a single identifier.
     */
    @Test
    void parseIdentifier() {
        final Scanner scanner = new TestScanner(new Token[]{new Token(TokenType.IDENT, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseIdentifier());
    }

    /**
     * The parser should not accept a number when expecting an identifier.
     */
    @Test
    void tryParseNoIdentifier() {
        final Scanner scanner = new TestScanner(new Token[]{new Token(TokenType.NUMBER, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertFalse(parser.parseIdentifier());
    }

    /**
     * This class serves as a mock for scanner and will likely be removed later on when Parsers can
     * accept token iterators.
     */
    private static class TestScanner extends Scanner {
        private final Token[] tokens;
        private int index;

        TestScanner(final Token[] tokens) {
            // the input that is not needed here
            super(Arrays.asList(new Character[]{' '}).iterator());
            this.tokens = tokens;
            index = -1; // one step before first token
        }

        /**
         * Simply advances the index inside the token array.
         */
        @Override
        public void processToken() {
            index++;
        }

        /**
         * @return the token at the current index
         */
        @Override
        public Token getSym() {
            return tokens[index];
        }
    }
}