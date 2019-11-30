package com.merkrafter.parsing;

import com.merkrafter.lexing.*;

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
}
