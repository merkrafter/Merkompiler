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
     * This method generates a stream of TokenWrappers that are valid assignments EXCEPT they're
     * lacking the ending semicolon.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    public static Stream<TokenWrapper> assignmentsWithoutSemicolon() {
        return Stream.of(
                // non-parameterized TokenWrappers
                Stream.of(
                        // direct assignment of a number to an identifier
                        // actual number value does not matter
                        new TokenWrapper().add(tokenFrom(TokenType.IDENT))
                                          .add(tokenFrom(TokenType.ASSIGN))
                                          .add(tokenFrom(TokenType.NUMBER)),

                        // direct assignment of an identifier to an identifier
                        new TokenWrapper().add(tokenFrom(TokenType.IDENT))
                                          .add(tokenFrom(TokenType.ASSIGN))
                                          .add(tokenFrom(TokenType.IDENT))),
                // parameterized TokenWrappers
                Stream.of(TokenType.PLUS, TokenType.MINUS, TokenType.TIMES, TokenType.DIVIDE)
                      .map(binOp -> new TokenWrapper().add(tokenFrom(TokenType.IDENT))
                                                      .add(tokenFrom(TokenType.ASSIGN))
                                                      .add(tokenFrom(TokenType.IDENT))
                                                      .add(tokenFrom(binOp))
                                                      .add(tokenFrom(TokenType.NUMBER))))
                     // merge all the above (outer) streams
                     .flatMap(i -> i);
    }

    /**
     * This method returns the same TokenWrappers as
     * the {@link #assignmentsWithoutSemicolon() assignmentsWithoutSemicolon} method does but with
     * semicolons appended. It therefore returns only valid assignments.
     *
     * @return a stream of TokenWrappers that define the test data
     */
    public static Stream<TokenWrapper> assignments() {
        return assignmentsWithoutSemicolon().map(tokenWrapper -> tokenWrapper.add(tokenFrom(
                TokenType.SEMICOLON)));
    }

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
