package com.merkrafter.lexing

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
         * The Tokenizer should be able to convert a single whitespace character to an OtherToken
         * with that exact same character.
         */
        @ParameterizedTest
        @ValueSource(strings = [" ", "\n", "\t"])
        fun `whitespace should be considered a Token`(s: String) {
            val input = sequenceOf(s.single())
            val expected = sequenceOf(OtherToken(s, "", 0, 0))
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
            val expectedToken = Token(TokenType.EOF, "", 0, 0)
            assertEquals(expectedToken, actualToken)
        }
    }

    private fun assertProduces(t: CharTokenizer, s: Sequence<Token>) {
        val sIterator = s.iterator()

        while (t.hasNext() && sIterator.hasNext()) {
            assertEquals(sIterator.next(), t.next())
        }

        // make sure that both iterators end here
        assertFalse(t.hasNext(), "tokenizer has more tokens; \"${t.next()}\" follows next")
        assertFalse(sIterator.hasNext())

    }
}