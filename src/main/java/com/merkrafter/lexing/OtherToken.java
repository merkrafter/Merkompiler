package com.merkrafter.lexing;

import org.jetbrains.annotations.NotNull;

/****
 * This class serves as a token and stores a string that could not be recognized as another token.
 *
 * @version v0.2.0
 * @author merkrafter
 ***************************************************************/
public class OtherToken extends Token {
  // ATTRIBUTES
  // ==============================================================
  /** the string that could not be recognized as another token */
  @NotNull private final String string;

  // CONSTRUCTORS
  // ==============================================================

  /****
   * Creates a new OtherToken from a string and position data.
   ***************************************************************/
  public OtherToken(
      @NotNull final String string,
      @NotNull final String filename,
      final long line,
      final int position) {
    super(TokenType.OTHER, filename, line, position);
    this.string = string;
  }

  // METHODS
  // ==============================================================
  // public methods
  // --------------------------------------------------------------

  /**
   * Two OtherTokens are equal if both have the type OtherToken and their strings, line numbers,
   * positions and filenames are equal.
   *
   * @param obj ideally a OtherToken to compare this with
   * @return whether this is equal to obj
   */
  @Override
  public boolean equals(@NotNull final Object obj) {
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
  @NotNull
  @Override
  public String toString() {
    return super.toString() + String.format("(%s)", string);
  }
}
