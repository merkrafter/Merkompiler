package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

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
    Type getReturnedType() {
        return Type.VOID;
    }
}
