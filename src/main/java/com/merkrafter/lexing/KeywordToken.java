package com.merkrafter.lexing;

/****
 * This class serves as a token and stores a keyword.
 *
 * @version v0.2.0
 * @author merkrafter
 ***************************************************************/
public class KeywordToken extends Token {
    // ATTRIBUTES
    //==============================================================
    /**
     * the keyword this token stands for
     */
    private final Keyword keyword;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new KeywordToken from a keyword and position data.
     ***************************************************************/
    public KeywordToken(final Keyword keyword, final String filename, final long line,
                        final int position) {
        super(TokenType.KEYWORD, filename, line, position);
        this.keyword = keyword;
    }

    // GETTER
    //==============================================================

    /**
     * @return the keyword this token stands for
     */
    Keyword getKeyword() {
        return keyword;
    }
}
