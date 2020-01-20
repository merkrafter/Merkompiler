package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

import java.util.LinkedList;
import java.util.List;

/****
 * This node class signals an error (a syntax or type error, for instance).
 * <p>
 * Implementing the Expression and Statement interfaces is more like a hack to allow
 * this node to be returned by the parser methods.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ErrorNode implements Expression, Statement {
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
     * This method is only here to fulfill the requirements for implementing Statement. It does not
     * have any meaningful implementation.
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
     * This method is only here to fulfill the requirements for implementing Statement. It does not
     * have any meaningful implementation.
     *
     * @return null
     */
    @Override
    public Statement getNext() {
        return null;
    }

    /**
     * This method is only here to fulfill the requirements for implementing Statement. It does not
     * have any meaningful implementation.
     *
     * @param next does not matter
     */
    @Override
    public void setNext(final Statement next) {
    }
}
