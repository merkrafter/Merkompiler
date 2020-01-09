package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

/****
 * This AST node represents an if-else construct.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class IfElseNode extends ASTBaseNode {
    // ATTRIBUTES
    //==============================================================
    private final IfNode ifNode;
    private final ASTBaseNode child;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new IfElseNode from an if node handling the if branch and a node that is executed
     * if the condition does not hold.
     * The constructor does not perform a type check.
     ***************************************************************/
    public IfElseNode(final IfNode ifNode, final ASTBaseNode child) {
        this.ifNode = ifNode;
        this.child = child;
    }

    // GETTER
    //==============================================================

    /**
     * Since an if-else construct does not return anything, this method always returns Type.VOID.
     *
     * @return Type.VOID
     */
    @Override
    Type getReturnedType() {
        return Type.VOID;
    }
}
