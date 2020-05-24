package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;
import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/****
 * This is the base node of all statement node types that an Abstract Syntax Tree
 * may be made of.
 * <p>
 * A Statement actually is a linked list of multiple statements that are executed in sequence.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public abstract class AbstractStatementNode implements Statement {
  // ATTRIBUTES
  // ==============================================================
  /** Next sequential instruction in the represented program. */
  @Nullable private Statement next;

  // CONSTRUCTORS
  // ==============================================================

  /****
   * Creates a new node without a next node assigned.
   ***************************************************************/
  public AbstractStatementNode() {}

  // GETTER
  // ==============================================================

  /** Next sequential instruction in the represented program. */
  @Nullable
  public Statement getNext() {
    return next;
  }

  // SETTER
  // ==============================================================

  /**
   * Sets the instruction that comes right after the one represented by this node.
   *
   * @param next the node that represents the instruction after this node's
   */
  public void setNext(@Nullable final Statement next) {
    this.next = next;
  }

  /**
   * This is a utility method that can be used by nodes to check their children for errors.
   *
   * @param nodes child node(s) that should be tested
   * @return a list of all errors that can be found in this subtree
   */
  @NotNull
  static List<String> collectErrorsFrom(final AbstractSyntaxTree... nodes) {
    final List<String> errors = new LinkedList<>();
    for (final AbstractSyntaxTree node : nodes) {
      if (node != null) {
        errors.addAll(node.getAllErrors());
      }
    }
    return errors;
  }

  @Override
  public abstract boolean equals(Object other);

  /** @return an identifier unique in the whole AST */
  @Override
  public int getID() {
    return hashCode();
  }

  /** @return dot/graphviz declarations of this component's next statement */
  @NotNull
  @Override
  public String getDotRepresentation() {
    if (getNext() == null) {
      return "";
    }
    return getNext().getDotRepresentation();
  }

  /** @return whether there is a return statement in this statement sequence */
  @Override
  public boolean hasReturnStatement() {
    if (getNext() == null) {
      return false;
    }
    return getNext().hasReturnStatement();
  }

  /** @return the type of the next statement in this sequence */
  @Override
  public boolean isCompatibleToType(@NotNull final Type type) {
    if (getNext() == null) {
      return type == Type.VOID;
    }
    return getNext().isCompatibleToType(type);
  }

  @NotNull
  @Override
  public List<String> getTypingErrors() {
    if (getNext() == null) {
      return new LinkedList<>();
    }
    return getNext().getTypingErrors();
  }
}
