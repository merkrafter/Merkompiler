package com.merkrafter.representation.ast;

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
        return collectErrorsFrom(variable, value);
    }

}
