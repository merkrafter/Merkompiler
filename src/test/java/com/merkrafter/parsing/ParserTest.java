package com.merkrafter.parsing;

import com.merkrafter.lexing.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    /**
     * The parser should accept a single "int" as a type.
     */
    @Test
    void parseType() {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.INT, null, 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseType());
    }

    /**
     * The parser should accept an assignment of the result of a binary operation to a variable,
     * as "a = a*5;".
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {"PLUS", "MINUS", "TIMES", "DIVIDE"})
    void parseAssignmentWithBinOp(final TokenType binOp) {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.IDENT, "", 0, 0),
                new Token(TokenType.ASSIGN, "", 0, 0),
                new Token(TokenType.IDENT, "", 0, 0),
                new Token(binOp, "", 0, 0),
                new Token(TokenType.NUMBER, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseAssignment());
    }

    /**
     * The parser should accept a direct assignment of a number to a variable ident, as "a = 5;".
     */
    @Test
    void parseDirectAssignmentOfNumber() {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.IDENT, "", 0, 0),
                new Token(TokenType.ASSIGN, "", 0, 0),
                new Token(TokenType.NUMBER, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseAssignment());
    }

    /**
     * The parser should accept a simple procedure call, as "parse();"
     */
    @Test
    void parseProcedureCall() {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.IDENT, "", 0, 0),
                new Token(TokenType.L_PAREN, "", 0, 0),
                new IdentToken("a", "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseProcedureCall());
    }


    /**
     * The parser should accept a single return statement.
     */
    @Test
    void parseStandaloneReturnStatement() {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.RETURN, null, 1, 1),
                new Token(TokenType.SEMICOLON, null, 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseReturnStatement());
    }

    /**
     * The parser should accept a single pair of parentheses as actual parameters.
     */
    @Test
    void parseEmptyActualParameters() {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.L_PAREN, "", 0, 0), new Token(TokenType.R_PAREN, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseActualParameters());
    }

    /**
     * The parser should accept a single comparison between an ident and a number as an expression.
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {
            "LOWER", "LOWER_EQUAL", "EQUAL", "GREATER_EQUAL", "GREATER"})
    void parseSingleComparisonAsExpression(final TokenType comparisonType) {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.IDENT, "", 0, 0),
                new Token(comparisonType, "", 0, 0),
                new Token(TokenType.NUMBER, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseExpression());
    }

    /**
     * The parser should accept a single addition/subtraction as a simple expression.
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {"PLUS", "MINUS"})
    void parseSimpleExpression(final TokenType tokenType) {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.IDENT, "", 0, 0),
                new Token(tokenType, "", 0, 0),
                new Token(TokenType.NUMBER, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseSimpleExpression());
    }

    /**
     * The parser should accept a single multiplication/division as a term.
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {"TIMES", "DIVIDE"})
    void parseTerm(final TokenType tokenType) {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.IDENT, "", 0, 0),
                new Token(tokenType, "", 0, 0),
                new Token(TokenType.NUMBER, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseTerm());
    }

    /**
     * The parser should accept an intern procedure call without arguments as a factor.
     */
    @Test
    void parseInternProcedureCallWithoutArgsAsFactor() {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.IDENT, "", 0, 0),
                new Token(TokenType.L_PAREN, "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseFactor());
    }

    /**
     * The parser should accept an intern procedure call with only one argument (number or ident)
     * as a factor.
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {"NUMBER", "IDENT"})
    void parseInternProcedureCallWithOneArgAsFactor(final TokenType tokenType) {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.IDENT, "", 0, 0),
                new Token(TokenType.L_PAREN, "", 0, 0),
                new Token(tokenType, "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseFactor());
    }

    /**
     * The parser should accept an intern procedure call with two comma-separated arguments (number
     * and an identifier) as a factor.
     */
    @Test
    void parseInternProcedureCallWithTwoArgsAsFactor() {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.IDENT, "", 0, 0),
                new Token(TokenType.L_PAREN, "", 0, 0),
                new Token(TokenType.NUMBER, "", 0, 0),
                new Token(TokenType.COMMA, "", 0, 0),
                new Token(TokenType.IDENT, "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseFactor());
    }

    /**
     * The parser should accept a simple expression representing a binary operation between a
     * number and an identifier as a factor.
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {"PLUS", "MINUS", "TIMES", "DIVIDE"})
    void parseBinOpExpressionAsFactor(final TokenType binOp) {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.L_PAREN, "", 0, 0),
                new Token(TokenType.NUMBER, "", 0, 0),
                new Token(binOp, "", 0, 0),
                new Token(TokenType.IDENT, "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseFactor());
    }

    /**
     * The parser should accept a simple expression (consisting only of parentheses and
     * identifier/number in between) as a factor.
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {"IDENT", "NUMBER"})
    void parseSimpleExpressionAsFactor(final TokenType tokenType) {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.L_PAREN, "", 0, 0),
                new Token(tokenType, "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseFactor());
    }

    /**
     * The parser should accept a single identifier or number as a factor.
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {"IDENT", "NUMBER"})
    void parseIdentifierOrNumberAsFactor(final TokenType tokenType) {
        final Scanner scanner = new TestScanner(new Token[]{new Token(tokenType, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseFactor());
    }

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
     * The parser should not accept another token when expecting a number.
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {"NUMBER"}, mode = EnumSource.Mode.EXCLUDE)
    void tryParseNoNumber(final TokenType tokenType) {
        final Scanner scanner = new TestScanner(new Token[]{new Token(tokenType, "", 0, 0)});
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
     * The parser should not accept another token when expecting an identifier.
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {"IDENT"}, mode = EnumSource.Mode.EXCLUDE)
    void tryParseNoIdentifier(final TokenType tokenType) {
        final Scanner scanner = new TestScanner(new Token[]{new Token(tokenType, "", 0, 0)});
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
         * @return the token at the current index or TokenType.EOF if index is out of bounds
         */
        @Override
        public Token getSym() {
            if (index == tokens.length) {
                return new Token(TokenType.EOF, "", 0, 0);
            } else {
                return tokens[index];
            }
        }
    }
}