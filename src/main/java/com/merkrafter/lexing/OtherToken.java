package com.merkrafter.lexing;

/****
 * This class serves as a token and stores a string that could not be recognized as another token.
 *
 * @version v0.2.0
 * @author merkrafter
 ***************************************************************/
public class OtherToken extends Token {
    // ATTRIBUTES
    //==============================================================
    /**
     * the string that could not be recognized as another token
     */
    private final String string;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new OtherToken from a string and position data.
     ***************************************************************/
    public OtherToken(final String string, final String filename, final long line,
                      final int position) {
        super(TokenType.OTHER, filename, line, position);
        this.string = string;
    }

    // GETTER
    //==============================================================

    /**
     * @return the string that could not be recognized as another token
     */
    String getString() {
        return string;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Two OtherTokens are equal if both have the type OtherToken and their strings, line
     * numbers, positions and filenames are equal.
     *
     * @param obj ideally a OtherToken to compare this with
     * @return whether this is equal to obj
     */
    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        return obj instanceof OtherToken && ((OtherToken) obj).string.equals(string);
    }

    /**
     * Creates a String representation of this OtherToken in the following format:
     * FILENAME(LINE,POSITION): TYPE(STRING)
     *
     * @return a String representation of this OtherToken
     */
    @Override
    public String toString() {
        return super.toString() + String.format("(%s)", string);
    }
}
