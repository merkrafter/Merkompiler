package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

/****
 * This AST node represents an if statement.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class IfNode extends ASTBaseNode {
    // ATTRIBUTES
    //==============================================================
    private final ASTBaseNode condition;
    private final ASTBaseNode child;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new IfNode from a node representing a condition and a node that is executed if the
     * condition holds.
     * The constructor does not perform a type check.
     ***************************************************************/
    public IfNode(final ASTBaseNode condition, final ASTBaseNode child) {
        this.condition = condition;
        this.child = child;
    }

    // GETTER
    //==============================================================

    /**
     * Since an if statement does not return anything, this method always returns Type.VOID.
     *
     * @return Type.VOID
     */
    @Override
    Type getReturnedType() {
        return Type.VOID;
    }
}
