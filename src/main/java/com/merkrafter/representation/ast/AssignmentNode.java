package com.merkrafter.representation.ast;

import java.util.List;

/****
 * This AST node represents the assignment of a value to a variable.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class AssignmentNode extends AbstractStatementNode {
    // ATTRIBUTES
    //==============================================================
    private final VariableAccessNode variable;
    private final Expression value;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new AssignmentNode from a node representing a value and a variable to assign
     * the value to.
     * The constructor does not perform a type check.
     ***************************************************************/
    public AssignmentNode(final VariableAccessNode variable, final Expression value) {
        this.variable = variable;
        this.value = value;
    }

    // GETTER
    //==============================================================

    /**
     * An AssignmentNode has a semantics error if the variable or expression is null or has an error
     * itself.
     *
     * @return whether the tree represented by this node has a semantics error somewhere
     */
    @Override
    public boolean hasSemanticsError() {
        return variable == null || value == null || variable.hasSemanticsError()
               || value.hasSemanticsError();
    }

    /**
     * An AssignmentNode has a syntax error if the variable or expression is null or has an error
     * itself.
     *
     * @return whether the tree represented by this node has a syntax error somewhere
     */
    @Override
    public boolean hasSyntaxError() {
        return variable == null || value == null || variable.hasSyntaxError()
               || value.hasSyntaxError();
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @Override
    public List<String> getAllErrors() {
        final List<String> errors = collectErrorsFrom(variable, value, getNext());
        if (variable.isConstant()) {
            // TODO: test this; it should not trigger ON that mentioned init
            errors.add("Can not assign a value to a constant after initialization");
        }
        return errors;
    }

    /**
     * Two AssignmentNodes are considered equal if their variables and values are non-null and are
     * equal to each other respectively.
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof AssignmentNode)) {
            return false;
        }
        final AssignmentNode other = (AssignmentNode) obj;
        return variable != null && other.variable != null && value != null && other.value != null
               && variable.equals(other.variable) && value.equals(other.value);
    }

    /**
     * @return dot/graphviz declarations of this component's children
     */
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
}
