package com.merkrafter.parsing;

import com.merkrafter.lexing.*;
import com.merkrafter.representation.ast.ConstantNode;
import com.merkrafter.representation.ast.ErrorNode;
import com.merkrafter.representation.ast.ParameterListNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    /**
     * The parser should accept "class MyClass {int a;}" as a class.
     */
    @Test
    void parseClass() {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.CLASS, null, 1, 1),
                new IdentToken("MyClass", null, 1, 1),
                new Token(TokenType.L_BRACE, null, 1, 1),
                new KeywordToken(Keyword.INT, null, 1, 1),
                new IdentToken("a", null, 1, 1),
                new Token(TokenType.SEMICOLON, null, 1, 1),
                new Token(TokenType.R_BRACE, null, 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseClass());
    }

    /**
     * The parser should accept a single "{int a;}" as a class body.
     */
    @Test
    void parseClassBody() {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.L_BRACE, null, 1, 1),
                new KeywordToken(Keyword.INT, null, 1, 1),
                new IdentToken("a", null, 1, 1),
                new Token(TokenType.SEMICOLON, null, 1, 1),
                new Token(TokenType.R_BRACE, null, 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseClassBody());
    }

    /**
     * The parser should accept a single "int a;" as a declaration.
     */
    @Test
    void parseDeclarations() {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.INT, null, 1, 1),
                new IdentToken("a", null, 1, 1),
                new Token(TokenType.SEMICOLON, null, 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseDeclarations());
    }

    /**
     * The parser should accept the declaration of a simple public void method without parameters
     * that only returns a constant number.
     */
    @ParameterizedTest
    @EnumSource(value = Keyword.class, names = {"VOID", "INT"})
    void parseMethodDeclaration(final Keyword methodType) {
        final Scanner scanner = new TestScanner(new Token[]{
                // method head
                new KeywordToken(Keyword.PUBLIC, null, 1, 1),
                new KeywordToken(methodType, null, 1, 1),
                new IdentToken("foo", null, 1, 1),
                new Token(TokenType.L_PAREN, null, 1, 1),
                new Token(TokenType.R_PAREN, null, 1, 1),
                // method body
                new Token(TokenType.L_BRACE, null, 1, 1),
                new KeywordToken(Keyword.RETURN, null, 1, 1),
                new Token(TokenType.NUMBER, null, 1, 1),
                new Token(TokenType.SEMICOLON, null, 1, 1),
                new Token(TokenType.R_BRACE, null, 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseMethodDeclaration());
    }

    /**
     * The parser should accept simple method heads without any formal parameters.
     */
    @ParameterizedTest
    @EnumSource(value = Keyword.class, names = {"VOID", "INT"})
    void parseMethodHead(final Keyword methodType) {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.PUBLIC, null, 1, 1),
                new KeywordToken(methodType, null, 1, 1),
                new IdentToken("foo", null, 1, 1),
                new Token(TokenType.L_PAREN, null, 1, 1),
                new Token(TokenType.R_PAREN, null, 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseMethodHead());
    }

    /**
     * The parser should accept "void" and "int" as method types.
     */
    @ParameterizedTest
    @EnumSource(value = Keyword.class, names = {"VOID", "INT"})
    void parseMethodType(final Keyword keyword) {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(keyword, null, 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseMethodType());
    }

    /**
     * The parser should accept a single "(int a)" as formal parameters.
     */
    @Test
    void parseFormalParameters() {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.L_PAREN, null, 1, 1),
                new KeywordToken(Keyword.INT, null, 1, 1),
                new IdentToken("a", null, 1, 1),
                new Token(TokenType.R_PAREN, null, 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseFormalParameters());
    }

    /**
     * The parser should accept a single "int a" as a fp_section.
     */
    @Test
    void parseFpSection() {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.INT, null, 1, 1), new IdentToken("a", null, 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseFpSection());
    }

    /**
     * The parser should accept a method body with only one return statement.
     */
    @Test
    void parseMethodBody() {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.L_BRACE, null, 1, 1),
                new KeywordToken(Keyword.RETURN, null, 1, 1),
                new Token(TokenType.NUMBER, null, 1, 1),
                new Token(TokenType.SEMICOLON, null, 1, 1),
                new Token(TokenType.R_BRACE, null, 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseMethodBody());
    }

    /**
     * The parser should accept a single "int a;" as a local declaration.
     */
    @Test
    void parseLocalDeclaration() {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.INT, null, 1, 1),
                new IdentToken("a", null, 1, 1),
                new Token(TokenType.SEMICOLON, null, 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseLocalDeclaration());
    }

    /**
     * The parser should be able to parse single statements as statement sequences.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#statements()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#statements")
    void parseStatementSequence(final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseStatementSequence());
    }

    /**
     * The parser should be able to parse statements.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#statements()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#statements")
    void parseStatement(final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseStatement());
    }

    /**
     * The parser should accept a single "int" as a type.
     */
    @Test
    void parseType() {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.INT, null, 1, 1)});
        final Parser parser = new Parser(scanner);
        assertNotNull(parser.parseType());
    }

    /**
     * The parser should be able to parse assignments.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#assignments()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#assignments")
    void parseAssignment(final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseAssignment());
    }

    /**
     * The parser should be able to detect syntactically wrong assignments.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#assignmentsWithoutSemicolon()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#assignmentsWithoutSemicolon")
    void parseFaultyAssignment(final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner);
        assertFalse(parser.parseAssignment());
    }

    /**
     * The parser should be able to parse procedure calls.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#procedureCalls()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#procedureCalls")
    void parseProcedureCall(final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseProcedureCall());
    }

    /**
     * The parser should be able to parse intern procedure calls.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#internProcedureCalls()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#internProcedureCalls")
    void parseInternProcedureCall(final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseInternProcedureCall());
    }

    /**
     * The parser should be able to parse simple if constructs.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#ifConstructs()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#ifConstructs")
    void parseIfStatement(final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseIfStatement());
    }

    /**
     * The parser should be able to parse simple while loops.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#whileLoops()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#whileLoops")
    void parseWhileStatement(final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseWhileStatement());
    }

    /**
     * The parser should be able to parse return statements.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#returnStatements()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#returnStatements")
    void parseReturnStatement(final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseReturnStatement());
    }

    /**
     * The parser must not accept a single return statement (without semicolon).
     */
    @Test
    void parseStandaloneReturnStatementWithoutSemicolon() {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.RETURN, null, 1, 1)});
        final Parser parser = new Parser(scanner);
        assertFalse(parser.parseReturnStatement());
    }

    /**
     * The parser should accept a single pair of parentheses as actual parameters.
     */
    @Test
    void parseEmptyActualParameters() {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.L_PAREN, "", 0, 0), new Token(TokenType.R_PAREN, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        final ParameterListNode parameters = parser.parseActualParameters();
        assertNotNull(parameters);
        assertTrue(parameters.getParameters().isEmpty());
    }

    /**
     * The parser should be able to parse expressions.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#expressions()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#expressions")
    void parseExpression(final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner);
        assertFalse(parser.parseExpression() instanceof ErrorNode);
    }

    /**
     * The parser should be able to parse simple expressions.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#simpleExpressions()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#simpleExpressions")
    void parseSimpleExpression(final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner);
        assertFalse(parser.parseSimpleExpression() instanceof ErrorNode);
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
        assertFalse(parser.parseTerm() instanceof ErrorNode);
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
        assertFalse(parser.parseFactor() instanceof ErrorNode);
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
        assertFalse(parser.parseFactor() instanceof ErrorNode);
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
        assertFalse(parser.parseFactor() instanceof ErrorNode);
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
        assertFalse(parser.parseFactor() instanceof ErrorNode);
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
        assertFalse(parser.parseFactor() instanceof ErrorNode);
    }

    /**
     * The parser should accept a single identifier or number as a factor.
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {"IDENT", "NUMBER"})
    void parseIdentifierOrNumberAsFactor(final TokenType tokenType) {
        final Scanner scanner = new TestScanner(new Token[]{new Token(tokenType, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertFalse(parser.parseFactor() instanceof ErrorNode);
    }

    /**
     * The parser should accept a single number.
     */
    @Test
    void parseNumber() {
        final long number = 5;
        final Scanner scanner = new TestScanner(new Token[]{new NumberToken(number, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseNumber() instanceof ConstantNode);
    }

    /**
     * The parser should not accept another token when expecting a number.
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {"NUMBER"}, mode = EnumSource.Mode.EXCLUDE)
    void tryParseNoNumber(final TokenType tokenType) {
        final Scanner scanner = new TestScanner(new Token[]{new Token(tokenType, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseNumber() instanceof ErrorNode);
    }

    /**
     * The parser should accept a single identifier.
     */
    @Test
    void parseIdentifier() {
        final Scanner scanner = new TestScanner(new Token[]{new Token(TokenType.IDENT, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertNotNull(parser.parseIdentifier());
    }

    /**
     * The parser should not accept another token when expecting an identifier.
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {"IDENT"}, mode = EnumSource.Mode.EXCLUDE)
    void tryParseNoIdentifier(final TokenType tokenType) {
        final Scanner scanner = new TestScanner(new Token[]{new Token(tokenType, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertNull(parser.parseIdentifier());
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