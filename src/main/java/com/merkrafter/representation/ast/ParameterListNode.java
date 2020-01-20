package com.merkrafter.representation.ast;

import java.util.LinkedList;
import java.util.List;

/****
 * This class is used to store values that can be passed to a procedure call.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ParameterListNode implements AbstractSyntaxTree {
    // ATTRIBUTES
    //==============================================================
    private final List<Expression> parameters;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new ParameterListNode.
     ***************************************************************/
    public ParameterListNode(final List<Expression> parameters) {
        this.parameters = parameters;
    }

    // GETTER
    //==============================================================

    /**
     * @return a list of expressions
     */
    public List<Expression> getParameters() {
        return parameters;
    }

    /**
     * A ParameterListNode can have an error if the underlying parameter list is null.
     *
     * @return whether a semantics error occurred
     */
    @Override
    public boolean hasSemanticsError() {
        return parameters == null;
    }

    /**
     * A ParameterListNode can have an error if the underlying parameter list is null.
     *
     * @return whether a syntax error occurred
     */
    @Override
    public boolean hasSyntaxError() {
        return parameters == null;
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @Override
    public List<String> getAllErrors() {
        final List<String> errors = new LinkedList<>();
        errors.add("Missing parameters");
        return errors;
    }
}
