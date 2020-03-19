package com.merkrafter.lexing;

import org.jetbrains.annotations.NotNull;

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
    public Keyword getKeyword() {
        return keyword;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Two KeywordTokens are equal if both have the type KeywordToken and their keywords, line
     * numbers, positions and filenames are equal.
     *
     * @param obj ideally a KeywordToken to compare this with
     * @return whether this is equal to obj
     */
    @Override
    public boolean equals(@NotNull final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        return obj instanceof KeywordToken && ((KeywordToken) obj).keyword == keyword;
    }

    /**
     * Creates a String representation of this KeywordToken in the following format:
     * FILENAME(LINE,POSITION): TYPE(KEYWORD)
     *
     * @return a String representation of this KeywordToken
     */
    @NotNull
    @Override
    public String toString() {
        return super.toString() + String.format("(%s)", keyword.name().toLowerCase());
    }
}
