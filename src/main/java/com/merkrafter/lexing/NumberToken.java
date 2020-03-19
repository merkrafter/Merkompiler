package com.merkrafter.lexing;

import org.jetbrains.annotations.NotNull;

/****
 * This class serves as a token and stores the (integer) number found.
 *
 * @version v0.2.0
 * @author merkrafter
 ***************************************************************/
public class NumberToken extends Token {
    // ATTRIBUTES
    //==============================================================
    /**
     * the number this token stands for
     */
    private final long number;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new NumberToken from a number and position data.
     ***************************************************************/
    public NumberToken(final long number, final String filename, final long line,
                       final int position) {
        super(TokenType.NUMBER, filename, line, position);
        this.number = number;
    }

    // GETTER
    //==============================================================

    /**
     * @return the number this token stands for
     */
    public long getNumber() {
        return number;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Two NumberTokens are equal if both have the type NumberToken and their numbers, line
     * numbers, positions and filenames are equal.
     *
     * @param obj ideally a NumberToken to compare this with
     * @return whether this is equal to obj
     */
    @Override
    public boolean equals(@NotNull final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        return obj instanceof NumberToken && ((NumberToken) obj).number == number;
    }

    /**
     * Creates a String representation of this NumberToken in the following format:
     * FILENAME(LINE,POSITION): TYPE(NUMBER)
     *
     * @return a String representation of this NumberToken
     */
    @NotNull
    @Override
    public String toString() {
        return super.toString() + String.format("(%d)", number);
    }
}
