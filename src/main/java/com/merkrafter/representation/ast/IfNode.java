package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

import java.util.List;

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
    public Type getReturnedType() {
        return Type.VOID;
    }

    /**
     * An IfNode has a semantics error if any of its child nodes is null or has an error itself.
     *
     * @return whether the tree represented by this node has a semantics error somewhere
     */
    @Override
    public boolean hasSemanticsError() {
        return condition == null || child == null || condition.hasSemanticsError()
               || child.hasSemanticsError();
    }

    /**
     * An IfNode has a syntax error if any of its child nodes is null or has an error itself.
     *
     * @return whether the tree represented by this node has a syntax error somewhere
     */
    @Override
    public boolean hasSyntaxError() {
        return condition == null || child == null || condition.hasSyntaxError()
               || child.hasSyntaxError();
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(condition, child);
    }

    /**
     * Two IfNodes are considered equal if their conditions and children are non-null and are
     * equal to each other respectively.
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof IfNode)) {
            return false;
        }
        final IfNode other = (IfNode) obj;
        return condition != null && other.condition != null && child != null && other.child != null
               && condition.equals(other.condition) && child.equals(other.child);
    }
}
