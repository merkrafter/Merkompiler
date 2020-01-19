package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

import java.util.List;

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
    private final AbstractSyntaxTree condition;
    private final AbstractSyntaxTree child;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new WhileNode from a node representing a condition and a node that is executed
     * while the condition holds.
     * The constructor does not perform a type check.
     ***************************************************************/
    public WhileNode(final AbstractSyntaxTree condition, final AbstractSyntaxTree child) {
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
    public Type getReturnedType() {
        return Type.VOID;
    }

    /**
     * A WhileNode has a semantics error if the child nodes are null or have errors themselves.
     *
     * @return whether the tree represented by this node has a semantics error somewhere
     */
    @Override
    public boolean hasSemanticsError() {
        return condition == null || child == null || condition.hasSemanticsError()
               || child.hasSemanticsError();
    }

    /**
     * A WhileNode has a syntax error if the child nodes are null or have errors themselves.
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
}
