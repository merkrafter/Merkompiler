package com.merkrafter.representation.ast;

import com.merkrafter.lexing.Position;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/****
 * This AST node represents the assignment of a value to a variable.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class AssignmentNode extends AbstractStatementNode {
  // ATTRIBUTES
  // ==============================================================
  @NotNull private final VariableAccessNode variable;
  @NotNull private final Expression value;

  // CONSTRUCTORS
  // ==============================================================

  /****
   * Creates a new AssignmentNode from a node representing a value and a variable to assign
   * the value to.
   * The constructor does not perform a type check.
   ***************************************************************/
  public AssignmentNode(
      @NotNull final VariableAccessNode variable, @NotNull final Expression value) {
    this.variable = variable;
    this.value = value;
  }

  // GETTER
  // ==============================================================

  /** @return a list of all errors, both semantic and syntactical ones. */
  @NotNull
  @Override
  public List<String> getAllErrors() {
    final List<String> errors = collectErrorsFrom(variable, value, getNext());
    if (variable.isConstant()) {
      errors.add(
          String.format(
              "%s: Can not assign a value to a constant after initialization", getPosition()));
    }
    return errors;
  }

  @NotNull
  @Override
  public Position getPosition() {
    return variable.getPosition();
  }

  /**
   * Two AssignmentNodes are considered equal if their variables and values are non-null and are
   * equal to each other respectively.
   */
  @Override
  public boolean equals(@NotNull final Object obj) {
    if (!(obj instanceof AssignmentNode)) {
      return false;
    }
    final AssignmentNode other = (AssignmentNode) obj;
    return variable.equals(other.variable) && value.equals(other.value);
  }

  /** @return dot/graphviz declarations of this component's children */
  @NotNull
  @Override
  public String getDotRepresentation() {
    // define next statement
    final StringBuilder dotRepr = new StringBuilder(super.getDotRepresentation());
    dotRepr.append(System.lineSeparator());

    // define children
    dotRepr.append(String.format("%d[label=%s];", variable.hashCode(), variable.getName()));
    dotRepr.append(System.lineSeparator());
    dotRepr.append(value.getDotRepresentation());
    dotRepr.append(System.lineSeparator());

    // define this node
    dotRepr.append(String.format("%d[label=%s];", getID(), "ASSIGN"));
    dotRepr.append(System.lineSeparator());

    // define links
    if (getNext() != null) {
      dotRepr.append(String.format("%d -> %d;", getID(), getNext().getID()));
      dotRepr.append(System.lineSeparator());
    }
    dotRepr.append(String.format("%d -> %d;", getID(), variable.hashCode()));
    dotRepr.append(System.lineSeparator());
    dotRepr.append(String.format("%d -> %d;", getID(), value.getID()));
    dotRepr.append(System.lineSeparator());

    return dotRepr.toString();
  }

  @NotNull
  @Override
  public List<String> getTypingErrors() {
    final List<String> errors = super.getTypingErrors();
    errors.addAll(value.getTypingErrors());
    if (!variable.getReturnedType().equals(value.getReturnedType())) {
      errors.add(
          String.format(
              "%s: Type mismatch in assignment to %s", getPosition(), variable.getName()));
    }
    return errors;
  }
}
