package com.merkrafter.lexing

import com.merkrafter.lexing.TokenType.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions.assumeFalse
import org.junit.jupiter.api.Assumptions.assumeTrue

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.concurrent.TimeUnit
import kotlin.NoSuchElementException
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

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
        fun `should recognize simple identifier as IdentToken`(expectedIdent: String) {
            val input = expectedIdent.asSequence()
            val tokenizer = CharTokenizer(input)
            val actualToken = tokenizer.next()
            assertTrue(actualToken is IdentToken)
            assertEquals(expectedIdent, actualToken.ident)
        }

        /**
         * The Tokenizer should be able to detect number arguments.
         * This test case makes sure that each digit is recognized as well as two- and three-digit
         * numbers and max long.
         */
        @ParameterizedTest
        @ValueSource(longs = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 321, Long.MAX_VALUE])
        fun `positive number should be recognized as NumberToken`(expectedNumber: Long) {
            val input = expectedNumber.toString().asSequence()
            val tokenizer = CharTokenizer(input)
            val actualToken = tokenizer.next()
            assertTrue(actualToken is NumberToken)
            assertEquals(expectedNumber, actualToken.number)
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
            val expected = sequenceOf(R_BRACE)
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
            val expected = sequenceOf(KEYWORD, IDENT, ASSIGN, NUMBER, SEMICOLON)
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

        /**
         * The Tokenizer should assign line 1, character position 1 to the first Token.
         * This case tests an identifier.
         */
        @Test
        fun `scan and assign correct starting position to identifier`() {
            val input = "a".asSequence()
            val tokenizer = CharTokenizer(input)
            val actualToken = tokenizer.next()
            val expectedToken = IdentToken("a", "", 1, 1)
            assertEquals(expectedToken, actualToken)
        }

        /**
         * The Tokenizer should assign line 1, character position 1 to the first Token.
         * This case tests a number.
         */
        @Test
        fun `scan and assign correct starting position to number`() {
            val number = 4L
            val input = "$number".asSequence()
            val tokenizer = CharTokenizer(input)
            val actualToken = tokenizer.next()
            val expectedToken = NumberToken(number, "", 1, 1)
            assertEquals(expectedToken, actualToken)
        }

        /**
         * The Tokenizer should assign column 2 after a space.
         */
        @Test
        fun `scan and assign correct position after space`() {
            val input = " ab".asSequence()
            val tokenizer = CharTokenizer(input)
            val expectedTokens = sequenceOf(IdentToken("ab", "", 1, 2))
            assertProduces(tokenizer, expectedTokens, checkOnlyType = false)
        }

        /**
         * The Tokenizer should assign line 2 after a line break.
         */
        @Test
        fun `scan and assign correct position after newline`() {
            val input = "a\nb".asSequence()
            val tokenizer = CharTokenizer(input)
            val expectedTokens = sequenceOf(IdentToken("a", "", 1, 1),
                    IdentToken("b", "", 2, 1))
            assertProduces(tokenizer, expectedTokens, checkOnlyType = false)
        }

        /**
         * This test case contains a more complex pattern of a newline and number, ident, and other
         * tokens that encode special characters.
         */
        @Test
        fun `scan and assign correct position to sequence of tokens`() {
            val input = "\n1e-14".asSequence()
            val tokenizer = CharTokenizer(input)
            val expectedTokens = sequenceOf(NumberToken(1L, "", 2, 1),
                    IdentToken("e", "", 2, 2),
                    Token(MINUS, "", 2, 3),
                    NumberToken(14L, "", 2, 4))
            assertProduces(tokenizer, expectedTokens, checkOnlyType = false)
        }

        /**
         * The Tokenizer should echo the given filename to several token types.
         */
        @Test
        fun `return given filename`() {
            val filename = "Input.java"
            val input = "1*a".asSequence()
            val tokenizer = CharTokenizer(input, filename)
            val expectedTokens = sequenceOf(NumberToken(1L, filename, 1, 1),
                    Token(TIMES, filename, 1, 2),
                    IdentToken("a", filename, 1, 3))
            assertProduces(tokenizer, expectedTokens, checkOnlyType = false)
        }

        /**
         * The Tokenizers hasNext method should return true if the input string contains an ident.
         */
        @Test
        fun `hasNext is true on string with ident`() {
            val input = "xyz".asSequence()
            val tokenizer = CharTokenizer(input)
            assertTrue(tokenizer.hasNext())
            tokenizer.next()
            assertFalse(tokenizer.hasNext())
        }

        /**
         * The Tokenizers hasNext method should return true if the input string contains an ident,
         * even if it is preceded by whitespace.
         */
        @Test
        fun `hasNext is true on string with ident after whitespace`() {
            val input = "   xyz".asSequence()
            val tokenizer = CharTokenizer(input)
            assertTrue(tokenizer.hasNext())
            tokenizer.next()
            assertFalse(tokenizer.hasNext())
        }

        /**
         * The Tokenizer should be convertible to a (finite) list of tokens if the input sequence is
         * finite as well.
         * If the Tokenizer returned an endless sequence of EOF tokens (as it used to do for some
         * time) it would cause unexpected behavior that decreases usability.
         */
        @Test
        /* Prevent the test from crashing with OutOfMemoryError in case the sequence is infinite.
        * Usually, the execution should not take longer than 200ms*/
        @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
        fun `should be able to create a list`() {
            val input = "class Test {final int a = 4;}" // arbitrary, non-trivial program code
            val tokenizer = CharTokenizer(input.asSequence())
            val actualTokenList = tokenizer.asSequence().toList()
            val expectedTokenList = listOf(KeywordToken(Keyword.CLASS, "", 1, 1),
                    IdentToken("Test", "", 1, 7),
                    Token(L_BRACE, "", 1, 12),
                    KeywordToken(Keyword.FINAL, "", 1, 13),
                    KeywordToken(Keyword.INT, "", 1, 19),
                    IdentToken("a", "", 1, 23),
                    Token(ASSIGN, "", 1, 25),
                    NumberToken(4L, "", 1, 27),
                    Token(SEMICOLON, "", 1, 28),
                    Token(R_BRACE, "", 1, 29))
            assertEquals(expectedTokenList, actualTokenList)
        }

        /**
         * A Tokenizer with an empty input string should return false when hasNext is called.
         * If next() is called in that situation anyway, a [NoSuchElementException] should be thrown
         * to comply with the [Iterator.next] interface.
         */
        @Test
        fun `throw a NoSuchElementException if next is called on empty string`() {
            val input = "".asSequence()
            val tokenizer = CharTokenizer(input)
            assumeFalse(tokenizer.hasNext())
            assertFailsWith<NoSuchElementException> { tokenizer.next() }
        }

        /**
         * A Tokenizer with a non-empty input string should return false when hasNext is called
         * after the input has been processed.
         * If next() is called in that situation anyway, a [NoSuchElementException] should be thrown
         * to comply with the [Iterator.next] interface.
         */
        @Test
        fun `throw a NoSuchElementException if next is called after hasNext is false`() {
            val input = "Test".asSequence()
            val tokenizer = CharTokenizer(input)
            assumeTrue(tokenizer.hasNext())
            tokenizer.next() // return value does not matter for this test case
            assumeFalse(tokenizer.hasNext())
            assertFailsWith<NoSuchElementException> { tokenizer.next() }
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
        fun `ignore asterisk in block comments`() {
            val input = "/***/".asSequence()
            val tokenizer = CharTokenizer(input)
            assertFalse(tokenizer.hasNext())
        }

        /**
         * The Tokenizer should ignore block comments that are at the end of a line and are not
         * completely closed but only by an asterisk.
         */
        @Test
        fun `scan and ignore not completely closed block comments at end of line`() {
            val input = "int a = 5;/*a really important variable*".asSequence()
            val expected = sequenceOf(KEYWORD, IDENT, ASSIGN, NUMBER, SEMICOLON)
                    .map { Token(it, "", 0, 0) }
            val tokenizer = CharTokenizer(input)
            assertProduces(tokenizer, expected)
        }

        /**
         * The Tokenizers hasNext method should return false on whitespace as there is no token
         * that could be returned via the following next() call.
         */
        @Test
        fun `hasNext is false on string with whitespace after ident`() {
            val input = "xyz   ".asSequence()
            val tokenizer = CharTokenizer(input)
            assertTrue(tokenizer.hasNext())
            val actualToken = tokenizer.next()
            assertTrue(actualToken is IdentToken)
            assertFalse(tokenizer.hasNext(), "Tokenizer.hasNext should have been false")
        }

        /**
         * The Tokenizers hasNext method should return false on a comment as there is no token
         * that could be returned via the following next() call.
         */
        @Test
        fun `hasNext is false on string with line comment after ident`() {
            val input = "xyz//def".asSequence()
            val tokenizer = CharTokenizer(input)
            assertTrue(tokenizer.hasNext())
            val actualToken = tokenizer.next()
            assertTrue(actualToken is IdentToken)
            assertFalse(tokenizer.hasNext(), "Tokenizer.hasNext should have been false")
        }

        /**
         * The Tokenizers hasNext method should return false on a comment as there is no token
         * that could be returned via the following next() call.
         */
        @Test
        fun `hasNext is false on string with block comment after ident`() {
            val input = "xyz/*def*/".asSequence()
            val tokenizer = CharTokenizer(input)
            assertTrue(tokenizer.hasNext())
            val actualToken = tokenizer.next()
            assertTrue(actualToken is IdentToken)
            assertFalse(tokenizer.hasNext(), "Tokenizer.hasNext should have been false")
        }
    }

    /**
     * Checks whether both Iterables produce the same sequence of Tokens.
     * @param checkOnlyType: if not set, .equals is called on the Tokens
     */
    private fun assertProduces(t: CharTokenizer, s: Sequence<Token>, checkOnlyType: Boolean = true) {
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
        assertFalse(t.hasNext(), "tokenizer has more tokens")
        assertFalse(sIterator.hasNext(), "sequence has more chars")
    }
}