package com.merkrafter.representation.ast;

import java.util.List;

/****
 * This AST node represents a while construct. It is very similar to the IfNode but can not
 * be interchanged with it as there is not while-else construct, for instance.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class WhileNode extends AbstractStatementNode {
    // ATTRIBUTES
    //==============================================================
    private final Expression condition;
    private final Statement loopBody;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new WhileNode from a node representing a condition and a node that is executed
     * while the condition holds.
     * The constructor does not perform a type check.
     ***************************************************************/
    public WhileNode(final Expression condition, final Statement loopBody) {
        this.condition = condition;
        this.loopBody = loopBody;
    }

    // GETTER
    //==============================================================

    /**
     * A WhileNode has a semantics error if the child nodes are null or have errors themselves.
     *
     * @return whether the tree represented by this node has a semantics error somewhere
     */
    @Override
    public boolean hasSemanticsError() {
        return condition == null || loopBody == null || condition.hasSemanticsError()
               || loopBody.hasSemanticsError();
    }

    /**
     * A WhileNode has a syntax error if the child nodes are null or have errors themselves.
     *
     * @return whether the tree represented by this node has a syntax error somewhere
     */
    @Override
    public boolean hasSyntaxError() {
        return condition == null || loopBody == null || condition.hasSyntaxError()
               || loopBody.hasSyntaxError();
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(condition, loopBody);
    }
}
