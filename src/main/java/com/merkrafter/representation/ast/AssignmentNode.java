package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

import java.util.List;

/****
 * This AST node represents the assignment of a value to a variable.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class AssignmentNode extends ASTBaseNode {
    // ATTRIBUTES
    //==============================================================
    private final VariableAccessNode variable;
    private final ASTBaseNode value;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new AssignmentNode from a node representing a value and a variable to assign
     * the value to.
     * The constructor does not perform a type check.
     ***************************************************************/
    public AssignmentNode(final VariableAccessNode variable, final ASTBaseNode value) {
        this.variable = variable;
        this.value = value;
    }

    // GETTER
    //==============================================================

    /**
     * Since an assignment does not return anything, this method always returns Type.VOID.
     *
     * @return Type.VOID
     */
    @Override
    public Type getReturnedType() {
        return Type.VOID;
    }

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
        return collectErrorsFrom(variable, value);
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
}
