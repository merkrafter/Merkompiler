package com.merkrafter.representation.ast;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/****
 * This class is used to store values that can be passed to a procedure call.
 * It is basically an AbstractSyntaxTree-implementing List
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ParameterListNode implements AbstractSyntaxTree {
    // ATTRIBUTES
    //==============================================================
    @NotNull
    private final List<Expression> parameters;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new ParameterListNode.
     ***************************************************************/
    public ParameterListNode(@NotNull final List<Expression> parameters) {
        this.parameters = parameters;
    }

    // GETTER
    //==============================================================

    /**
     * @return a list of expressions
     */
    @NotNull
    public List<Expression> getParameters() {
        return parameters;
    }

    /**
     * @return false
     */
    @Override
    public boolean hasSemanticsError() {
        return false;
    }

    /**
     * @return false
     */
    @Override
    public boolean hasSyntaxError() {
        return false;
    }

    /**
     * @return empty list
     */
    @NotNull
    @Override
    public List<String> getAllErrors() {
        return new LinkedList<>();
    }

    /**
     * Two ParameterListNodes are considered equal if their parameter lists are non-null and are
     * equal to each other.
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ParameterListNode)) {
            return false;
        }
        final ParameterListNode other = (ParameterListNode) obj;
        return parameters.equals(other.parameters);
    }
}
