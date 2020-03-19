package com.merkrafter.lexing;

import org.jetbrains.annotations.NotNull;

/****
 * This class serves as a token and stores the identifier found.
 *
 * @version v0.2.0
 * @author merkrafter
 ***************************************************************/
public class IdentToken extends Token {
    // ATTRIBUTES
    //==============================================================
    /**
     * the identifier this token stands for
     */
    private final String ident;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new IdentToken from an identifier and position data.
     ***************************************************************/
    public IdentToken(final String ident, final String filename, final long line,
                      final int position) {
        super(TokenType.IDENT, filename, line, position);
        this.ident = ident;
    }

    // GETTER
    //==============================================================

    /**
     * @return the identifier this token stands for
     */
    public String getIdent() {
        return ident;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Two IdentTokens are equal if both have the type IdentToken and their identifiers, line
     * numbers, positions and filenames are equal.
     *
     * @param obj ideally a IdentToken to compare this with
     * @return whether this is equal to obj
     */
    @Override
    public boolean equals(@NotNull final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        return obj instanceof IdentToken && ((IdentToken) obj).ident.equals(ident);
    }

    /**
     * Creates a String representation of this IdentToken in the following format:
     * FILENAME(LINE,POSITION): TYPE(IDENT)
     *
     * @return a String representation of this IdentToken
     */
    @NotNull
    @Override
    public String toString() {
        return super.toString() + String.format("(%s)", ident);
    }
}
