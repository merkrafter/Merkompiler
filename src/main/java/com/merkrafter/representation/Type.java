package com.merkrafter.representation;

import org.jetbrains.annotations.NotNull;

/****
 * This class stores information on a type in JavaSST.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public enum Type {
  INT(0),
  BOOLEAN(false),
  VOID(0);

  @NotNull private final Object defaultValue;

  Type(@NotNull final Object defaultValue) {
    this.defaultValue = defaultValue;
  }

  @NotNull
  public Object getDefaultValue() {
    return defaultValue;
  }
}
