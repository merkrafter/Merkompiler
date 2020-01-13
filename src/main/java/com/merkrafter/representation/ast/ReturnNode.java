package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

/****
 * This class represents a return statement.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ReturnNode extends ASTBaseNode {
    // ATTRIBUTES
    //==============================================================
    private final ASTBaseNode expression;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new return node with the given expression as its value.
     ***************************************************************/
    public ReturnNode(final ASTBaseNode expression) {
        this.expression = expression;
    }

    // GETTER
    //==============================================================
    @Override
    public Type getReturnedType() {
        return expression.getReturnedType();
    }
}
