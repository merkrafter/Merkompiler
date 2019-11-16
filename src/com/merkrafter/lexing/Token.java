package com.merkrafter.lexing;

/****
 * This class represents a token that is emitted by a Scanner.
 * It holds read-only context information.
 *
 * @author merkrafter
 ***************************************************************/
public class Token {
    // ATTRIBUTES
    //==============================================================
    private final TokenType type;
    private final String filename;
    private final long line;
    private final int position;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new Token that stores important about information
     * for the Parser.
     ***************************************************************/
    public Token(final TokenType type, final String filename, final long line, final int position) {
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
    public TokenType getType() {
        return type;
    }

    /**
     * @return the file this token is located in
     */
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
}
