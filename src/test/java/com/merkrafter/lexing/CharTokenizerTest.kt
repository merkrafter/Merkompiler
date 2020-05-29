package com.merkrafter.lexing

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class CharTokenizerTest {

    @Nested
    inner class EdgeCases {
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