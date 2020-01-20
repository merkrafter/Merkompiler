package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

import java.util.List;

/****
 * This class represents a return statement.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ReturnNode extends ASTBaseNode {
    // ATTRIBUTES
    //==============================================================
    private final Expression expression;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new return node without an expression.
     ***************************************************************/
    public ReturnNode() {
        this(null);
    }

    /****
     * Creates a new return node with the given expression as its value.
     ***************************************************************/
    public ReturnNode(final Expression expression) {
        this.expression = expression;
    }

    // GETTER
    //==============================================================

    /**
     * @return the type of the expression or VOID if this return does not have an expression
     */
    public Type getReturnedType() {
        if (expression == null) {
            return Type.VOID;
        }
        return expression.getReturnedType();
    }

    /**
     * A ReturnNode has a semantics error if the return value exists but has an error.
     *
     * @return whether the tree represented by this node has a semantics error somewhere
     */
    @Override
    public boolean hasSemanticsError() {
        return expression != null && expression.hasSemanticsError();
    }

    /**
     * A ReturnNode can not have a syntax error.
     *
     * @return false
     */
    @Override
    public boolean hasSyntaxError() {
        return false;
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(expression);
    }
}
