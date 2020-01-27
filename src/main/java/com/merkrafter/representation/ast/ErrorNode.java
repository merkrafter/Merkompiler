package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

import java.util.LinkedList;
import java.util.List;

/****
 * This node class signals an error (a syntax or type error, for instance).
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ErrorNode extends ASTBaseNode {
    // ATTRIBUTES
    //==============================================================
    private final String message;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new ErrorNode with a given error message.
     ***************************************************************/
    public ErrorNode(final String message) {
        this.message = message;
    }

    // GETTER
    //==============================================================

    /**
     * Returns the error message.
     *
     * @return the error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Errors do not have any types hence this method always returns Type.VOID.
     *
     * @return Type.VOID
     */
    @Override
    public Type getReturnedType() {
        return Type.VOID;
    }

    /**
     * An ErrorNode is always counted as an error.
     *
     * @return true
     */
    @Override
    public boolean hasSemanticsError() {
        return true;
    }

    /**
     * An ErrorNode is always counted as an error.
     *
     * @return true
     */
    @Override
    public boolean hasSyntaxError() {
        return true;
    }

    /**
     * @return a list containing the error message of this node
     */
    @Override
    public List<String> getAllErrors() {
        final List<String> errors = new LinkedList<>();
        errors.add(message);
        return errors;
    }

    /**
     * An object is considered equal to an ErrorNode if and only if it is an ErrorNode itself.
     */
    @Override
    public boolean equals(final Object other) {
        return other instanceof ErrorNode;
    }
}
