package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

import java.util.List;

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
    public Type getReturnedType() {
        return Type.VOID;
    }

    /**
     * An IfElseNode has a semantics error if the child nodes are null or have an error themselves.
     *
     * @return whether the tree represented by this node has a semantics error somewhere
     */
    @Override
    public boolean hasSemanticsError() {
        return ifNode == null || child == null || ifNode.hasSemanticsError()
               || child.hasSemanticsError();
    }

    /**
     * An IfElseNode has a syntax error if the child nodes are null or have an error themselves.
     *
     * @return whether the tree represented by this node has a syntax error somewhere
     */
    @Override
    public boolean hasSyntaxError() {
        return ifNode == null || child == null || ifNode.hasSyntaxError() || child.hasSyntaxError();
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(ifNode, child);
    }
}
