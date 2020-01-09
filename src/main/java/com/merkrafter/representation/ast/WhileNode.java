package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

/****
 * This AST node represents a while construct. It is very similar to the IfNode but can not
 * be interchanged with it as there is not while-else construct, for instance.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class WhileNode extends ASTBaseNode {
    // ATTRIBUTES
    //==============================================================
    private final ASTBaseNode condition;
    private final ASTBaseNode child;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new WhileNode from a node representing a condition and a node that is executed
     * while the condition holds.
     * The constructor does not perform a type check.
     ***************************************************************/
    public WhileNode(final ASTBaseNode condition, final ASTBaseNode child) {
        this.condition = condition;
        this.child = child;
    }

    // GETTER
    //==============================================================

    /**
     * Since a while loop does not return anything, this method always returns Type.VOID.
     *
     * @return Type.VOID
     */
    @Override
    Type getReturnedType() {
        return Type.VOID;
    }
}
