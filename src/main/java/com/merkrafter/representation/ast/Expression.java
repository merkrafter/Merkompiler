package com.merkrafter.representation.ast;

import com.merkrafter.lexing.Locatable;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.graphical.GraphicalComponent;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/****
 * An expression is anything that can be evaluated to a type.
 *
 * @since v0.4.0
 * @author merkrafter
 ***************************************************************/
public interface Expression extends AbstractSyntaxTree, GraphicalComponent, Locatable {
  // METHODS
  // ==============================================================
  // public methods
  // --------------------------------------------------------------

  /** @return the type that this expression evaluates to */
  @NotNull
  Type getReturnedType();

  @NotNull
  List<String> getTypingErrors();
}
