package com.merkrafter.representation.ast;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.Type;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/****
 * This AST node represents the access to an unnamed constant.
 * <p>
 * Currently, this class does not guarantee that the type and the value's type are equal.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ConstantNode<T> implements Expression {
    // ATTRIBUTES
    //==============================================================
    @NotNull
    private final Type type;
    @NotNull
    private final T value;
    @NotNull
    private final Position position;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new node that represents an access to an unnamed constant.
     * Currently, it is not checked whether either of the arguments are null or whether
     * their types match.
     ***************************************************************/
    public ConstantNode(@NotNull final Type type, @NotNull final T value,
                        @NotNull final Position position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    // GETTER
    //==============================================================

    /**
     * After evaluating this node, this is the type that is propagated upwards.
     *
     * @return the return type of this node
     */
    @NotNull
    @Override
    public Type getReturnedType() {
        return type;
    }

    @NotNull
    @Override
    public Position getPosition() {
        return position;
    }

    /**
     * @return empty list
     */
    @NotNull
    @Override
    public List<String> getTypingErrors() {
        return new LinkedList<>();
    }

    /**
     * Returns the value stored by this constant node.
     *
     * @return value stored by this constant node
     */
    @NotNull
    public T getValue() {
        return value;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Returns whether this node is equal to the other constant node.
     * Both constant nodes are equal if their types and values are equal.
     *
     * @param o optimally a ConstantNode instance
     * @return whether this is equal to o
     */
    @Override
    public boolean equals(@NotNull final Object o) {
        if (this == o) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        final ConstantNode<?> that = (ConstantNode<?>) o;
        return type == that.type && Objects.equals(getValue(), that.getValue());
    }

    /**
     * @return an empty list
     */
    @NotNull
    @Override
    public List<String> getAllErrors() {
        return new LinkedList<>();
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
    @Override
    public String getDotRepresentation() {
        return String.format("%d[label=\"%s%s%s\"];",
                             getID(),
                             type.name(),
                             System.lineSeparator(),
                             value.toString());
    }
}
