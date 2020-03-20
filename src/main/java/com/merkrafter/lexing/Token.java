package com.merkrafter.lexing;

import org.jetbrains.annotations.NotNull;

/****
 * This class represents a token that is emitted by a Scanner.
 * It holds read-only context information.
 *
 * @author merkrafter
 ***************************************************************/
public class Token {
    // ATTRIBUTES
    //==============================================================
    @NotNull
    private final TokenType type;
    @NotNull
    private final String filename;
    private final long line;
    private final int position;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new Token that stores important about information
     * for the Parser.
     ***************************************************************/
    public Token(@NotNull final TokenType type, @NotNull final String filename, final long line, final int position) {
        this.type = type;
        this.filename = filename;
        this.line = line;
        this.position = position;
    }

    // GETTER
    //==============================================================

    /**
     * @return the type of this Token
     */
    @NotNull
    public TokenType getType() {
        return type;
    }

    /**
     * @return the file this token is located in
     */
    @NotNull
    public String getFilename() {
        return filename;
    }

    /**
     * @return the line number inside the file this token is located in
     */
    public long getLine() {
        return line;
    }

    /**
     * @return the position inside the line this token is located in
     */
    public int getPosition() {
        return position;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Two tokens are equal if both have the type Token and their line numbers, positions and
     * filenames are equal
     *
     * @param obj ideally a Token to compare this with
     * @return whether this is equal to obj
     */
    @Override
    public boolean equals(@NotNull final Object obj) {
        if (obj instanceof Token) {
            final Token other = (Token) obj;
            return eqTypes(other) && eqLines(other) && eqPositions(other) && eqFilenames(other);
        }
        return false;
    }

    /**
     * @return whether both types are equal
     */
    private boolean eqTypes(@NotNull final Token other) {
        return type == other.type;
    }

    /**
     * @return whether both line numbers are equal
     */
    private boolean eqLines(@NotNull final Token other) {
        return line == other.line;
    }

    /**
     * @return whether both positions are equal
     */
    private boolean eqPositions(@NotNull final Token other) {
        return position == other.position;
    }

    /**
     * @return whether both Token's filenames are null or equal
     */
    private boolean eqFilenames(@NotNull final Token other) {
        // both have a value
        return filename.equals(other.filename);
    }

    /**
     * Creates a String representation of this Token in the following format:
     * FILENAME(LINE,POSITION): TYPE
     * @return a String representation of this Token
     */
    @NotNull
    @Override
    public String toString() {
        return String.format("%s(%d,%d): %s", filename, line, position, type);
    }
}
