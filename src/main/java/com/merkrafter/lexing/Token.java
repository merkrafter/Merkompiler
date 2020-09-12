package com.merkrafter.lexing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/****
 * This class represents a token that is emitted by a Scanner.
 * It holds read-only context information.
 *
 * @author merkrafter
 ***************************************************************/
public class Token {
  // ATTRIBUTES
  // ==============================================================
  @NotNull private final TokenType type;
  @NotNull private final Position position;

  // CONSTRUCTORS
  // ==============================================================

  /****
   * Creates a new Token that stores important about information
   * for the Parser.
   ***************************************************************/
  public Token(
      @NotNull final TokenType type,
      @NotNull final String filename,
      final long line,
      final int position) {
    this.type = type;
    this.position = new Position(filename, line, position);
  }

  // GETTER
  // ==============================================================

  /** @return the type of this Token */
  @NotNull
  public TokenType getType() {
    return type;
  }

  /** @return the position this token is located at */
  @NotNull
  public Position getPosition() {
    return position;
  }

  // METHODS
  // ==============================================================
  // public methods
  // --------------------------------------------------------------

  /**
   * Two tokens are equal if both have the type Token and their line numbers, positions and
   * filenames are equal
   *
   * @param obj ideally a Token to compare this with
   * @return whether this is equal to obj
   */
  @Override
  public boolean equals(@Nullable final Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj instanceof Token) {
      final Token other = (Token) obj;
      return eqTypes(other) && position.equals(other.position);
    }
    return false;
  }

  /** @return whether both types are equal */
  private boolean eqTypes(@NotNull final Token other) {
    return type.equals(other.type);
  }

  /**
   * Creates a String representation of this Token in the following format: FILENAME(LINE,POSITION):
   * TYPE
   *
   * @return a String representation of this Token
   */
  @NotNull
  @Override
  public String toString() {
    return String.format("%s: %s", position, type);
  }
}
