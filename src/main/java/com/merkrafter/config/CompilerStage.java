package com.merkrafter.config;

import org.jetbrains.annotations.NotNull;

/****
 * This enum represents the steps the compiler goes through in order to convert a JavaSST source
 * code file into actual byte code.
 *
 * @version v0.2.0
 * @author merkrafter
 ***************************************************************/
public enum CompilerStage {
  // CONSTANTS
  // ==============================================================
  /** Only scan the input and output tokens. */
  SCANNING,
  /** Scan and parse the input and output whether this was successful. */
  PARSING;

  /** @return the lowercase name of this enum item */
  @NotNull
  @Override
  public String toString() {
    return name().toLowerCase();
  }

  /**
   * Returns the latest stage this enum currently offers in terms of processing data.
   *
   * @return the latest available compiler stage
   */
  @NotNull
  public static CompilerStage latest() {
    return PARSING;
  }
}
