package com.merkrafter.lexing;

import java.util.Iterator;

/**
 * This class provides the possibility to iterate over strings. It can be used as a tool to mock a
 * file as the input to a Scanner.
 *
 * @author merkrafter
 * @since v0.1.0
 */
public class StringIteratorTestUtility implements Iterator<Character> {
  /**
   * The string to iterate over. It is not set by the constructor as the input string will not be
   * known during the setUp method anyway.
   */
  private String string = "";
  /** Tracks the index in the string. */
  private int index = 0;

  public void setString(String string) {
    this.string = string;
  }

  /** @return true if there are more characters in the string */
  @Override
  public boolean hasNext() {
    return index < string.length();
  }

  /**
   * Returns the next character in the string if there is any left. This should be checked via
   * hasNext() before this method is called in order to avoid an exception.
   *
   * @return next character in the string
   * @throws IndexOutOfBoundsException if there are no more characters left to read
   */
  @Override
  public Character next() {
    return string.charAt(index++);
  }
}
