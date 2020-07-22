package com.merkrafter.lexing

import com.merkrafter.lexing.TokenType.*

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource

internal class CharTokenizerTest {

    /**
     * The test cases in this class test normal behavior in scenarios that will appear almost always
     * in real source files.
     */
    @Nested
    inner class HappyPaths {
        /**
         * The Tokenizer should be able to detect identifiers.
         * These start with a letter, either lower or upper case, and may continue with a
         * combination of letters and/or digits.
         */
        @ParameterizedTest
        @ValueSource(strings = ["a", "B", "xyz", "someIdentifier", "l33t"])
        fun `should recognize simple identifier as IdentToken`(s: String) {
            val input = s.asSequence()
            val expected = sequenceOf(IdentToken(s, "", 0, 0))
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should be able to detect number arguments.
         * This test case makes sure that each digit is recognized as well as two- and three-digit
         * numbers and max long.
         */
        @ParameterizedTest
        @ValueSource(longs = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 321, Long.MAX_VALUE])
        fun `positive number should be recognized as NumberToken`(n: Long) {
            val input = n.toString().asSequence()
            val expected = sequenceOf(NumberToken(n, "", 0, 0))
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should be able to recognize keywords.
         *
         * In particular, these should be tokenized into KeywordTokens instead of IdentTokens.
         * All possible keywords are listed in the [Keyword] enum.
         */
        @ParameterizedTest
        @EnumSource(Keyword::class)
        fun `a keyword should be recognized as KeywordToken`(keyword: Keyword) {
            val input = keyword.name.toLowerCase().asSequence()
            val expected = sequenceOf(KeywordToken(keyword, "", 0, 0))
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should be able to recognize special characters.
         *
         * They are listed in [TokenType] and include symbols like +, ==, or ;
         */
        @ParameterizedTest
        @EnumSource(TokenType::class,
                mode = EnumSource.Mode.EXCLUDE,
                names = ["KEYWORD", "IDENT", "NUMBER", "EOF", "OTHER"])
        fun `special symbols should be recognized as their respective token`(tokenType: TokenType) {
            //the above enum source should only include valid enum items anyway
            val input = tokenType.symbol!!.asSequence()
            val expected = sequenceOf(Token(tokenType, "", 0, 0))
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        @Test
        fun `scan arithmetic expression without whitespace`() {
            val input = "a+(b-c)*d/e;".asSequence()
            val expected = sequenceOf(IDENT, PLUS, L_PAREN, IDENT, MINUS, IDENT, R_PAREN,
                    TIMES, IDENT, DIVIDE, IDENT, SEMICOLON).map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        @Test
        fun `scan assignment expression with whitespace`() {
            val input = "int result = a + ( b - c ) * d / e;".asSequence()
            val expected = sequenceOf(KEYWORD, IDENT, ASSIGN, IDENT, PLUS, L_PAREN, IDENT, MINUS, IDENT, R_PAREN,
                    TIMES, IDENT, DIVIDE, IDENT, SEMICOLON).map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        @Test
        fun `scan simple assignment with whitespace`() {
            val input = "int\n\t  a  \n=\n5\t\t\t  ;".asSequence()
            val expected = sequenceOf(KEYWORD, IDENT, ASSIGN, NUMBER, SEMICOLON).map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should ignore line comments. These start with `//` and end with an EOL
         * character.
         */
        @Test
        fun `scan and ignore line comments before code line`() {
            val input = " //in mph\nint velocity;".asSequence()
            val expected = sequenceOf(KEYWORD, IDENT, SEMICOLON).map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should ignore line comments. These start with `//` and end with an EOL
         * character.
         */
        @Test
        fun `scan and ignore line comments between code lines`() {
            val input = "int velocity; //in mph\nint acceleration;".asSequence()
            val expected = sequenceOf(KEYWORD, IDENT, SEMICOLON, KEYWORD, IDENT, SEMICOLON)
                    .map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should ignore line comments. These start with `//` and end with an EOL
         * character.
         */
        @Test
        fun `scan and ignore line comments at the end of code lines`() {
            val input = "} //end of main class".asSequence()
            val expected = sequenceOf(R_BRACE, EOF)
                    .map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should ignore block comments that are mid-line. These start with `/*` and
         * end with `*/`.
         */
        @Test
        fun `scan and ignore block comments in line`() {
            val input = "int a /*a really important variable*/ = 5;".asSequence()
            val expected = sequenceOf(KEYWORD, IDENT, ASSIGN, NUMBER, SEMICOLON)
                    .map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should ignore block comments that are at the end of a line.
         */
        @Test
        fun `scan and ignore block comments at end of line`() {
            val input = "int a = 5;/*a really important variable*/".asSequence()
            val expected = sequenceOf(KEYWORD, IDENT, ASSIGN, NUMBER, SEMICOLON, EOF)
                    .map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should ignore block comments that span multiple lines.
         */
        @Test
        fun `scan and ignore block comments over multiple lines`() {
            val input = "/*\nThis is a description of the method\n*/public void draw();".asSequence()
            val expected = sequenceOf(KEYWORD, KEYWORD, IDENT, L_PAREN, R_PAREN, SEMICOLON)
                    .map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should correctly scan comparisons.
         */
        @ParameterizedTest
        @EnumSource(TokenType::class,
                mode = EnumSource.Mode.INCLUDE,
                names = ["LOWER_EQUAL", "LOWER", "EQUAL", "GREATER", "GREATER_EQUAL"])
        fun `scan comparison conditions`(op: TokenType) {
            val input = "if(a${op.symbol}b)".asSequence()
            val expected = sequenceOf(KEYWORD, L_PAREN, IDENT, op, IDENT, R_PAREN)
                    .map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should be able to scan an empty main procedure.
         * This is not valid JavaSST code, but it is used here as it contains square brackets.
         */
        @Test
        fun `scan empty main procedure`() {
            val input = "public void main(String[] args) {}".asSequence()
            val expected = sequenceOf(KEYWORD, KEYWORD, IDENT, L_PAREN, IDENT, L_SQ_BRACKET,
                    R_SQ_BRACKET, IDENT, R_PAREN, L_BRACE, R_BRACE)
                    .map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should be able to scan an empty class.
         */
        @Test
        fun `scan empty class`() {
            val input = "class Test {}".asSequence()
            val expected = sequenceOf(KEYWORD, IDENT, L_BRACE, R_BRACE)
                    .map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should be able to scan a method call with multiple call arguments.
         * This includes scanning commas.
         */
        @Test
        fun `scan method call with two arguments`() {
            val input = "int sum = add(a,1)".asSequence()
            val expected = sequenceOf(KEYWORD, IDENT, ASSIGN, IDENT, L_PAREN, IDENT, COMMA, NUMBER, R_PAREN)
                    .map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }
    }

    @Nested
    inner class EdgeCases {
        /**
         * The Tokenizer should use base 10 to interpret numbers.
         * `010` was chosen as test data because this could be interpreted as 8 in the octal system.
         * Here, the expected result is the decimal 10 (ten).
         */
        @ParameterizedTest
        @ValueSource(strings = ["010", "0010"])
        fun `always interpret numbers with base 10`(s: String) {
            val input = s.asSequence()
            val expected = sequenceOf(NumberToken(10, "", 0, 0))
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should be able to convert a single special character to an OtherToken
         * with that exact same character, even though these special characters are not part of the
         * language definition.
         */
        @ParameterizedTest
        @ValueSource(strings = ["$", "\"", "@", "_", "!", "§", "%", "&", "|", "^", "\\", "?", "~", "#"])
        fun `foreign special character should be considered a Token`(s: String) {
            val input = sequenceOf(s.single())
            val expected = sequenceOf(OtherToken(s, "", 0, 0))
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should be able to convert a whitespace character to an EOF token as there
         * is no real other token to read.
         */
        @ParameterizedTest
        @ValueSource(strings = [" ", "\n", "\t"])
        fun `whitespace should be considered a Token`(s: String) {
            val input = sequenceOf(s.single())
            val expected = sequenceOf(Token(EOF, "", 0, 0))
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * A Tokenizer with an empty input string should return false when hasNext is called.
         * If next() is called in that situation anyway, an EOF token should be returned.
         */
        @Test
        fun `produce EOF token on empty string`() {
            val input = "".asSequence()
            val tokenizer = CharTokenizer(input)
            assertFalse(tokenizer.hasNext())
            val actualToken = tokenizer.next()
            val expectedToken = Token(EOF, "", 0, 0)
            assertEquals(expectedToken, actualToken)
        }

        /**
         * The Tokenizer should not scan two slashes with space in between as a line comment.
         */
        @Test
        fun `do not scan separated slashes as line comment`() {
            val input = " / /velocity;".asSequence()
            val expected = sequenceOf(DIVIDE, DIVIDE, IDENT, SEMICOLON)
                    .map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should not scan a slash and an asterisk with space in between as a
         * block comment.
         */
        @Test
        fun `do not scan separated slash and asterisk as block comment`() {
            val input = " / *velocity;".asSequence()
            val expected = sequenceOf(DIVIDE, TIMES, IDENT, SEMICOLON)
                    .map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The scanner should not make errors lexing an asterisk followed by a / outside a comment.
         */
        @Test
        fun `do not scan recognize asterisk and slash as end outside block comment`() {
            val input = " */ velocity".asSequence()
            val expected = sequenceOf(TIMES, DIVIDE, IDENT)
                    .map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should ignore block comments that contain an asterisk.
         */
        @Test
        fun `scan and ignore asterisk in block comments`() {
            val input = "/***/".asSequence()
            val expected = sequenceOf(EOF)
                    .map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizer should ignore block comments that are at the end of a line and are not
         * completely closed but only by an asterisk.
         */
        @Test
        fun `scan and ignore not completely closed block comments at end of line`() {
            val input = "int a = 5;/*a really important variable*".asSequence()
            val expected = sequenceOf(KEYWORD, IDENT, ASSIGN, NUMBER, SEMICOLON, EOF)
                    .map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }
    }

    /**
     * Checks whether both Iterables produce the same sequence of Tokens.
     * @param checkOnlyType: if not set, .equals is called on the Tokens
     */
    private fun assertProduces(t: CharTokenizer, s: Sequence<Token>, checkOnlyType: Boolean = false) {
        val sIterator = s.iterator()

        while (t.hasNext() && sIterator.hasNext()) {
            val expected = sIterator.next()
            val actual = t.next()
            if (checkOnlyType) {
                assertEquals(expected.type, actual.type)
            } else {
                assertEquals(expected, actual)
            }
        }

        // make sure that both iterators end here
        assertFalse(t.hasNext(), "tokenizer has more tokens; \"${t.next()}\" follows next")
        assertFalse(sIterator.hasNext())
    }
}