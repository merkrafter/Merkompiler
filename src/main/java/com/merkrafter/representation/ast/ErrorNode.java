package com.merkrafter.representation.ast;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @NotNull
    private final String message;
    @NotNull
    private final Position position;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new ErrorNode with a given error message and a dummy position.
     ***************************************************************/
    public ErrorNode(@NotNull final String message) {
        this(message, new Position("", 0, 0));
    }

    /****
     * Creates a new ErrorNode with a given error message.
     ***************************************************************/
    public ErrorNode(@NotNull final String message, @NotNull final Position position) {
        this.message = message;
        this.position = position;
    }

    // GETTER
    //==============================================================

    @NotNull
    @Override
    public Position getPosition() {
        return position;
    }

    /**
     * This method is only here to fulfill the requirements for implementing Statement. It does not
     * have any meaningful implementation.
     *
     * @return Type.VOID
     */
    @NotNull
    @Override
    public Type getReturnedType() {
        return Type.VOID;
    }

    @NotNull
    @Override
    public List<String> getTypingErrors() {
        return new LinkedList<>();
    }

    /**
     * @return a list containing the error message of this node
     */
    @NotNull
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
    @Nullable
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
    public void setNext(@Nullable final Statement next) {
    }

    /**
     * @return an identifier unique in the whole AST
     */
    @Override
    public int getID() {
        return hashCode();
    }

    /**
     * @return dot/graphviz declarations of this component's children
     */
    @NotNull
    @Override
    public String getDotRepresentation() {
        return String.format("%d[label=\"ERROR\"]", getID());
    }

    /**
     * @return false
     */
    @Override
    public boolean isCompatibleToType(@NotNull final Type type) {
        return false;
    }

    @NotNull
    @Override
    public String toString() {
        return message;
    }
}
