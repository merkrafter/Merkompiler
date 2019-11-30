package com.merkrafter.parsing;

import com.merkrafter.lexing.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/****
 * This class serves as a test data provider for ParserTest.
 *
 * @version v0.2.0
 * @author merkrafter
 ***************************************************************/

public class ParserTestDataProvider {
    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------
    /**
     * Creates a new Token from a TokenType by setting file name, line and position number to some
     * default values in order to make increase the readability of test cases.
     *
     * @return a basic Token with the given TokenType set
     */
    static Token tokenFrom(final TokenType type) {
        return new Token(type, null, 1, 1);
    }

    /**
     * Creates a new Token from a Keyword by setting file name, line and position number to some
     * default values in order to make increase the readability of test cases.
     *
     * @return a KeywordToken with the given Keyword set
     */
    static Token tokenFrom(final Keyword keyword) {
        return new KeywordToken(keyword, null, 1, 1);
    }

    /**
     * Creates a new Token from an identifier string by setting file name, line and position number
     * to some default values in order to make increase the readability of test cases.
     *
     * @return an IdentToken with the given identifier set
     */
    static Token tokenFrom(final String identifier) {
        return new IdentToken(identifier, null, 1, 1);
    }

    /**
     * Creates a new Token from a number string by setting file name, line and position number to
     * some default values in order to make increase the readability of test cases.
     *
     * @return a NumberToken with the given number set
     */
    static Token tokenFrom(final long number) {
        return new NumberToken(number, null, 1, 1);
    }

    /**
     * This class serves as a wrapper around a list of tokens as directly passing around lists/arrays
     * of tokens from the provider to the test methods does not work, as they're merged into one big
     * stream of Tokens.
     */
    static class TokenWrapper {
        private List<Token> tokenList;

        TokenWrapper() {
            tokenList = new LinkedList<>();
        }

        /**
         * Adds the given token at the end of the token list and returns this TokenWrapper instance.
         *
         * @param token a token to append to this token wrapper
         * @return itself in order to allow chaining
         */
        TokenWrapper add(final Token token) {
            tokenList.add(token);
            return this;
        }

        /**
         * @return the stored tokens as an array
         */
        Token[] getTokens() {
            return tokenList.toArray(new Token[0]);
        }

        /**
         * @return the string representation of the underlying token list
         */
        @Override
        public String toString() {
            return tokenList.toString();
        }
    }
}
