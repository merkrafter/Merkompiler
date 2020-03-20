package com.merkrafter.parsing;

import com.merkrafter.lexing.*;
import com.merkrafter.representation.SymbolTable;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.VariableDescription;
import com.merkrafter.representation.ast.AbstractSyntaxTree;
import com.merkrafter.representation.ast.ConstantNode;
import com.merkrafter.representation.ast.ErrorNode;
import com.merkrafter.representation.ast.ParameterListNode;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    public static final String CLASS_IDENT = "TestClass";
    public static final String VAR_IDENT = "testVar";
    public static final String PROC_IDENT = "testMethod";

    /**
     * The parser should accept "class {@value #CLASS_IDENT} {int {@value #VAR_IDENT};}" as a class.
     */
    @Test
    void parseClass() {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.CLASS, "", 1, 1),
                new IdentToken(CLASS_IDENT, "", 1, 1),
                new Token(TokenType.L_BRACE, "", 1, 1),
                new KeywordToken(Keyword.INT, "", 1, 1),
                new IdentToken(VAR_IDENT, "", 1, 1),
                new Token(TokenType.SEMICOLON, "", 1, 1),
                new Token(TokenType.R_BRACE, "", 1, 1)});
        final Parser parser = new Parser(scanner);
        assertFalse(parser.parseClass().hasSyntaxError());
    }

    /**
     * The parser should accept a single "{int {@value #VAR_IDENT};}" as a class body.
     */
    @Test
    void parseClassBody() throws ParserException {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.L_BRACE, "", 1, 1),
                new KeywordToken(Keyword.INT, "", 1, 1),
                new IdentToken(VAR_IDENT, "", 1, 1),
                new Token(TokenType.SEMICOLON, "", 1, 1),
                new Token(TokenType.R_BRACE, "", 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseClassBody());
    }

    /**
     * The parser should accept a single "int {@value #VAR_IDENT}" as a declaration.
     */
    @Test
    void parseDeclarations() throws ParserException {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.INT, "", 1, 1),
                new IdentToken(VAR_IDENT, "", 1, 1),
                new Token(TokenType.SEMICOLON, "", 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseDeclarations());
    }

    /**
     * The parser should accept the declaration of a simple public void method without parameters
     * that only returns a constant number.
     */
    @ParameterizedTest
    @EnumSource(value = Keyword.class, names = {"VOID", "INT"})
    void parseMethodDeclaration(@NotNull final Keyword methodType) throws ParserException {
        final Scanner scanner = new TestScanner(new Token[]{
                // method head
                new KeywordToken(Keyword.PUBLIC, "", 1, 1),
                new KeywordToken(methodType, "", 1, 1),
                new IdentToken(PROC_IDENT, "", 1, 1),
                new Token(TokenType.L_PAREN, "", 1, 1),
                new Token(TokenType.R_PAREN, "", 1, 1),
                // method body
                new Token(TokenType.L_BRACE, "", 1, 1),
                new KeywordToken(Keyword.RETURN, "", 1, 1),
                new Token(TokenType.NUMBER, "", 1, 1),
                new Token(TokenType.SEMICOLON, "", 1, 1),
                new Token(TokenType.R_BRACE, "", 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseMethodDeclaration());
    }

    /**
     * The parser should accept simple method heads without any formal parameters.
     */
    @ParameterizedTest
    @EnumSource(value = Keyword.class, names = {"VOID", "INT"})
    void parseMethodHead(@NotNull final Keyword methodType) {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.PUBLIC, "", 1, 1),
                new KeywordToken(methodType, "", 1, 1),
                new IdentToken(PROC_IDENT, "", 1, 1),
                new Token(TokenType.L_PAREN, "", 1, 1),
                new Token(TokenType.R_PAREN, "", 1, 1)});
        final Parser parser = new Parser(scanner);
        assertNotNull(parser.parseMethodHead());
    }

    /**
     * The parser should accept "void" and "int" as method types.
     */
    @ParameterizedTest
    @EnumSource(value = Keyword.class, names = {"VOID", "INT"})
    void parseMethodType(@NotNull final Keyword keyword) {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(keyword, "", 1, 1)});
        final Parser parser = new Parser(scanner);
        assertNotNull(parser.parseMethodType());
    }

    /**
     * The parser should accept a single "(int {@value #VAR_IDENT}" as formal parameters.
     */
    @Test
    void parseFormalParameters() {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.L_PAREN, "", 1, 1),
                new KeywordToken(Keyword.INT, "", 1, 1),
                new IdentToken(VAR_IDENT, "", 1, 1),
                new Token(TokenType.R_PAREN, "", 1, 1)});
        final Parser parser = new Parser(scanner);
        assertNotNull(parser.parseFormalParameters());
    }

    /**
     * The parser should accept a single "int {@value #VAR_IDENT} as a fp_section.
     */
    @Test
    void parseFpSection() {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.INT, "", 1, 1), new IdentToken(VAR_IDENT, "", 1, 1)});
        final Parser parser = new Parser(scanner);
        assertNotNull(parser.parseFpSection());
    }

    /**
     * The parser should accept a method body with only one return statement.
     */
    @Test
    void parseMethodBody() throws ParserException {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.L_BRACE, "", 1, 1),
                new KeywordToken(Keyword.RETURN, "", 1, 1),
                new Token(TokenType.NUMBER, "", 1, 1),
                new Token(TokenType.SEMICOLON, "", 1, 1),
                new Token(TokenType.R_BRACE, "", 1, 1)});
        final Parser parser = new Parser(scanner);
        assertFalse(parser.parseMethodBody() instanceof ErrorNode);
    }

    /**
     * The parser should accept a single "int {@value #VAR_IDENT}" as a local declaration.
     */
    @Test
    void parseLocalDeclaration() throws ParserException {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.INT, "", 1, 1),
                new IdentToken(VAR_IDENT, "", 1, 1),
                new Token(TokenType.SEMICOLON, "", 1, 1)});
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
    void parseStatementSequence(@NotNull final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner, ParserTestDataProvider.TEST_SYMBOLS);
        assertFalse(parser.parseStatementSequence().hasSyntaxError());
    }

    /**
     * The parser should be able to parse statements.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#statements()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#statements")
    void parseStatement(@NotNull final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner, ParserTestDataProvider.TEST_SYMBOLS);
        final AbstractSyntaxTree node = parser.parseStatement();
        assertFalse(node.hasSyntaxError());
    }

    /**
     * The parser should accept a single "int" as a type.
     */
    @Test
    void parseType() {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.INT, "", 1, 1)});
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
    void parseAssignment(@NotNull final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner, ParserTestDataProvider.TEST_SYMBOLS);
        final AbstractSyntaxTree astUnderTest = parser.parseAssignment();
        assertFalse(astUnderTest.hasSyntaxError());
    }

    /**
     * The parser should be able to detect syntactically wrong assignments.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#assignmentsWithoutSemicolon()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#assignmentsWithoutSemicolon")
    void parseFaultyAssignment(@NotNull final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseAssignment().hasSyntaxError());
    }

    /**
     * The parser should be able to parse simple if constructs.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#ifConstructs()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#ifConstructs")
    void parseIfStatement(@NotNull final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner, ParserTestDataProvider.TEST_SYMBOLS);
        final AbstractSyntaxTree node = parser.parseIfStatement();
        assertFalse(node.hasSyntaxError());
    }

    /**
     * The parser should be able to parse simple while loops.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#whileLoops()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#whileLoops")
    void parseWhileStatement(@NotNull final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner, ParserTestDataProvider.TEST_SYMBOLS);
        assertFalse(parser.parseWhileStatement().hasSyntaxError());
    }

    /**
     * The parser should be able to parse return statements.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#returnStatements()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#returnStatements")
    void parseReturnStatement(@NotNull final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner, ParserTestDataProvider.TEST_SYMBOLS);
        assertFalse(parser.parseReturnStatement().hasSyntaxError());
    }

    /**
     * The parser must not accept a single return statement (without semicolon).
     */
    @Test
    void parseStandaloneReturnStatementWithoutSemicolon() {
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.RETURN, "", 1, 1)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseReturnStatement().hasSyntaxError());
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
    void parseExpression(@NotNull final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner, ParserTestDataProvider.TEST_SYMBOLS);
        assertFalse(parser.parseExpression().hasSyntaxError());
    }

    /**
     * The parser should be able to parse simple expressions.
     *
     * @param inputTokens token lists provided by {@link ParserTestDataProvider#simpleExpressions()}
     */
    @ParameterizedTest
    @MethodSource("com.merkrafter.parsing.ParserTestDataProvider#simpleExpressions")
    void parseSimpleExpression(@NotNull final ParserTestDataProvider.TokenWrapper inputTokens) {
        final Scanner scanner = new TestScanner(inputTokens.getTokens());
        final Parser parser = new Parser(scanner, ParserTestDataProvider.TEST_SYMBOLS);
        assertFalse(parser.parseSimpleExpression().hasSyntaxError());
    }

    /**
     * The parser should accept a single multiplication/division as a term.
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {"TIMES", "DIVIDE"})
    void parseTerm(@NotNull final TokenType tokenType) {
        final Scanner scanner = new TestScanner(new Token[]{
                new IdentToken("a", "", 0, 0),
                new Token(tokenType, "", 0, 0),
                new Token(TokenType.NUMBER, "", 0, 0)});
        final Parser parser = new Parser(scanner, ParserTestDataProvider.TEST_SYMBOLS);
        assertFalse(parser.parseTerm().hasSyntaxError());
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
        assertFalse(parser.parseFactor().hasSyntaxError());
    }

    /**
     * The parser should accept an intern procedure call with only one argument (number or ident)
     * as a factor.
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {"NUMBER", "IDENT"})
    void parseInternProcedureCallWithOneArgAsFactor(@NotNull final TokenType tokenType) {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.IDENT, "", 0, 0),
                new Token(TokenType.L_PAREN, "", 0, 0),
                new Token(tokenType, "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertFalse(parser.parseFactor().hasSyntaxError());
    }

    /**
     * The parser should accept an intern procedure call with two comma-separated arguments (number
     * and an identifier) as a factor.
     */
    @Test
    void parseInternProcedureCallWithTwoArgsAsFactor() {
        final Scanner scanner = new TestScanner(new Token[]{
                new IdentToken("a", "", 0, 0),
                new Token(TokenType.L_PAREN, "", 0, 0),
                new Token(TokenType.NUMBER, "", 0, 0),
                new Token(TokenType.COMMA, "", 0, 0),
                new Token(TokenType.IDENT, "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0)});
        final Parser parser = new Parser(scanner, ParserTestDataProvider.TEST_SYMBOLS);
        assertFalse(parser.parseFactor().hasSyntaxError());
    }

    /**
     * The parser should accept a simple expression representing a binary operation between a
     * number and an identifier as a factor.
     */
    @ParameterizedTest
    @EnumSource(value = TokenType.class, names = {"PLUS", "MINUS", "TIMES", "DIVIDE"})
    void parseBinOpExpressionAsFactor(@NotNull final TokenType binOp) {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.L_PAREN, "", 0, 0),
                new Token(TokenType.NUMBER, "", 0, 0),
                new Token(binOp, "", 0, 0),
                new IdentToken("a", "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0)});
        final Parser parser = new Parser(scanner, ParserTestDataProvider.TEST_SYMBOLS);
        final AbstractSyntaxTree node = parser.parseFactor();
        assertFalse(node.hasSyntaxError());
    }

    /**
     * The parser should accept a simple expression (consisting only of parentheses and
     * identifier in between) as a factor.
     */
    @Test
    void parseSimpleExpressionWithIdentAsFactor() {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.L_PAREN, "", 0, 0),
                new IdentToken("a", "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0)});
        final Parser parser = new Parser(scanner, ParserTestDataProvider.TEST_SYMBOLS);
        assertFalse(parser.parseFactor().hasSyntaxError());
    }

    /**
     * The parser should accept a simple expression (consisting only of parentheses and
     * number in between) as a factor.
     */
    @Test
    void parseSimpleExpressionWithNumberAsFactor() {
        final Scanner scanner = new TestScanner(new Token[]{
                new Token(TokenType.L_PAREN, "", 0, 0),
                new NumberToken(5, "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertFalse(parser.parseFactor().hasSyntaxError());
    }

    /**
     * The parser should accept a single identifier as a factor.
     */
    @Test
    void parseIdentifierAsFactor() {
        final Scanner scanner = new TestScanner(new Token[]{new IdentToken("a", "", 0, 0)});
        final Parser parser = new Parser(scanner, ParserTestDataProvider.TEST_SYMBOLS);
        assertFalse(parser.parseFactor().hasSyntaxError());
    }

    /**
     * The parser should accept a single number as a factor.
     */
    @Test
    void parseNumberAsFactor() {
        final Scanner scanner = new TestScanner(new Token[]{new NumberToken(0, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertFalse(parser.parseFactor().hasSyntaxError());
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
    void tryParseNoNumber(@NotNull final TokenType tokenType) {
        final Scanner scanner = new TestScanner(new Token[]{new Token(tokenType, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertTrue(parser.parseNumber().hasSyntaxError());
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
    void tryParseNoIdentifier(@NotNull final TokenType tokenType) {
        final Scanner scanner = new TestScanner(new Token[]{new Token(tokenType, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertNull(parser.parseIdentifier());
    }

    /**
     * The scanner should accept two variables with the same names if they are in different scopes.
     * It does not matter whether the outer variable is constant or not.
     */
    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    void testTwoVariablesWithSameNamesInDifferentScopes(@NotNull final String constant) {
        final String name = "a";
        final SymbolTable outerScope = new SymbolTable();
        outerScope.insert(new VariableDescription(name, Type.INT, 0, Boolean.getBoolean(constant)));
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.INT, "", 0, 0),
                new IdentToken(name, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0)});
        final Parser parser = new Parser(scanner, outerScope);
        assertDoesNotThrow(parser::parseLocalDeclaration);
    }

    /**
     * The scanner should indicate an error if two variables with the same names were declared
     * in the same scope.
     */
    @Test
    void testTwoVariablesWithSameNames() throws ParserException {
        final String name = "a";
        final Scanner scanner = new TestScanner(new Token[]{
                new KeywordToken(Keyword.INT, "", 0, 0),
                new IdentToken(name, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0),
                new KeywordToken(Keyword.INT, "", 0, 0),
                new IdentToken(name, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        parser.parseLocalDeclaration(); // parse first variable and store it in the symbol table
        assertThrows(ParserException.class, parser::parseLocalDeclaration);
    }

    /**
     * The scanner should indicate an error if two variables with the same names were declared
     * in the same scope (class level).
     */
    @Test
    void testTwoVariablesWithSameNamesInClass() {
        final String name = "a";
        final Scanner scanner = new TestScanner(new Token[]{
                //final int a = 0;
                new KeywordToken(Keyword.FINAL, "", 0, 0),
                new KeywordToken(Keyword.INT, "", 0, 0),
                new IdentToken(name, "", 0, 0),
                new Token(TokenType.ASSIGN, "", 0, 0),
                new NumberToken(0, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0),
                // int a;
                new KeywordToken(Keyword.INT, "", 0, 0),
                new IdentToken(name, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertThrows(ParserException.class, parser::parseDeclarations);
    }

    /**
     * The scanner should indicate an error if two variables with the same names were declared
     * in the same scope when both are final (class level).
     */
    @Test
    void testTwoFinalVariablesWithSameNamesInClass() {
        final String name = "a";
        final Scanner scanner = new TestScanner(new Token[]{
                // final int a = 0;
                new KeywordToken(Keyword.FINAL, "", 0, 0),
                new KeywordToken(Keyword.INT, "", 0, 0),
                new IdentToken(name, "", 0, 0),
                new Token(TokenType.ASSIGN, "", 0, 0),
                new NumberToken(0, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0),
                // final int a = 0;
                new KeywordToken(Keyword.FINAL, "", 0, 0),
                new KeywordToken(Keyword.INT, "", 0, 0),
                new IdentToken(name, "", 0, 0),
                new Token(TokenType.ASSIGN, "", 0, 0),
                new NumberToken(0, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertThrows(ParserException.class, parser::parseDeclarations);
    }

    /**
     * The scanner should indicate an error if two procedures with the same names were declared.
     */
    @Test
    void testTwoProceduresWithSameNames() {
        final String name = "a";
        final Scanner scanner = new TestScanner(new Token[]{
                // public void a(){return;}
                new KeywordToken(Keyword.PUBLIC, "", 0, 0),
                new KeywordToken(Keyword.VOID, "", 0, 0),
                new IdentToken(name, "", 0, 0),
                new Token(TokenType.L_PAREN, "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0),
                new Token(TokenType.L_BRACE, "", 0, 0),
                new KeywordToken(Keyword.RETURN, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0),
                new Token(TokenType.R_BRACE, "", 0, 0),
                // public void a(){return;}
                new KeywordToken(Keyword.PUBLIC, "", 0, 0),
                new KeywordToken(Keyword.VOID, "", 0, 0),
                new IdentToken(name, "", 0, 0),
                new Token(TokenType.L_PAREN, "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0),
                new Token(TokenType.L_BRACE, "", 0, 0),
                new KeywordToken(Keyword.RETURN, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0),
                new Token(TokenType.R_BRACE, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertThrows(ParserException.class, parser::parseDeclarations);
    }

    /**
     * The scanner should NOT indicate an error if two procedures with the same names were declared
     * if their formal parameters differ.
     */
    @Test
    void testTwoProceduresWithSameNamesButDifferentFormalParameters() {
        final String name = "a";
        final Scanner scanner = new TestScanner(new Token[]{
                // public void a(){return;}
                new KeywordToken(Keyword.PUBLIC, "", 0, 0),
                new KeywordToken(Keyword.VOID, "", 0, 0),
                new IdentToken(name, "", 0, 0),
                new Token(TokenType.L_PAREN, "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0),
                new Token(TokenType.L_BRACE, "", 0, 0),
                new KeywordToken(Keyword.RETURN, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0),
                new Token(TokenType.R_BRACE, "", 0, 0),
                // public void a(int var){return;}
                new KeywordToken(Keyword.PUBLIC, "", 0, 0),
                new KeywordToken(Keyword.VOID, "", 0, 0),
                new IdentToken(name, "", 0, 0),
                new Token(TokenType.L_PAREN, "", 0, 0),
                new KeywordToken(Keyword.INT, "", 0, 0),
                new IdentToken("var", "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0),
                new Token(TokenType.L_BRACE, "", 0, 0),
                new KeywordToken(Keyword.RETURN, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0),
                new Token(TokenType.R_BRACE, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertDoesNotThrow(parser::parseDeclarations);
    }

    /**
     * The scanner should indicate an error if two formal parameters with the same names were
     * declared in the same procedure.
     */
    @Test
    void testTwoFormalParametersWithSameNames() {
        final String name = "a";
        final Scanner scanner = new TestScanner(new Token[]{
                // public void func(int a, int a){return;}
                new KeywordToken(Keyword.PUBLIC, "", 0, 0),
                new KeywordToken(Keyword.VOID, "", 0, 0),
                new IdentToken("func", "", 0, 0),
                new Token(TokenType.L_PAREN, "", 0, 0),
                new KeywordToken(Keyword.INT, "", 0, 0),
                new IdentToken(name, "", 0, 0),
                new Token(TokenType.COMMA, "", 0, 0),
                new KeywordToken(Keyword.INT, "", 0, 0),
                new IdentToken(name, "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0),
                new Token(TokenType.L_BRACE, "", 0, 0),
                new KeywordToken(Keyword.RETURN, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0),
                new Token(TokenType.R_BRACE, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertThrows(ParserException.class, parser::parseMethodDeclaration);
    }

    /**
     * The scanner should indicate an error if a local variable with the same name as a formal
     * parameter was declared in the same procedure.
     */
    @Test
    void testLocalVariableAndFormalParameterWithSameNames() {
        final String name = "a";
        final Scanner scanner = new TestScanner(new Token[]{
                // public void func(int a){int a; return;}
                new KeywordToken(Keyword.PUBLIC, "", 0, 0),
                new KeywordToken(Keyword.VOID, "", 0, 0),
                new IdentToken("func", "", 0, 0),
                new Token(TokenType.L_PAREN, "", 0, 0),
                new KeywordToken(Keyword.INT, "", 0, 0),
                new IdentToken(name, "", 0, 0),
                new Token(TokenType.R_PAREN, "", 0, 0),
                new Token(TokenType.L_BRACE, "", 0, 0),
                new KeywordToken(Keyword.INT, "", 0, 0),
                new IdentToken(name, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0),
                new KeywordToken(Keyword.RETURN, "", 0, 0),
                new Token(TokenType.SEMICOLON, "", 0, 0),
                new Token(TokenType.R_BRACE, "", 0, 0)});
        final Parser parser = new Parser(scanner);
        assertThrows(ParserException.class, parser::parseMethodDeclaration);
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
        @NotNull
        @Override
        public Token getSym() {
            if (index == tokens.length) {
                return new Token(TokenType.EOF, "", 0, 0);
            } else {
                return tokens[index];
            }
        }

        @NotNull
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tokens.length; i++) {
                if (i == index) {
                    sb.append(String.format("[%s], ", tokens[i]));
                } else {
                    sb.append(String.format("%s, ", tokens[i]));

                }
            }
            return sb.toString();
        }
    }
}