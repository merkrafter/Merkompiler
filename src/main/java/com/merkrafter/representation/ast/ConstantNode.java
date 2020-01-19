package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;
import com.merkrafter.representation.VariableDescription;

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
public class ConstantNode<T> extends ASTBaseNode {
    // ATTRIBUTES
    //==============================================================
    private final Type type;
    private final T value;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new node that represents an access to an unnamed constant.
     * Currently, it is not checked whether either of the arguments are null or whether
     * their types match.
     ***************************************************************/
    public ConstantNode(final Type type, final T value) {
        this.type = type;
        this.value = value;
    }

    // GETTER
    //==============================================================

    /**
     * After evaluating this node, this is the type that is propagated upwards.
     *
     * @return the return type of this node
     */
    @Override
    public Type getReturnedType() {
        return type;
    }

    /**
     * Returns the value stored by this constant node.
     *
     * @return value stored by this constant node
     */
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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ConstantNode<?> that = (ConstantNode<?>) o;
        return type == that.type && Objects.equals(getValue(), that.getValue());
    }

    /**
     * A ConstantNode can not have a semantics error.
     *
     * @return false
     */
    @Override
    public boolean hasSemanticsError() {
        return false;
    }

    /**
     * A ConstantNode can not have a syntax error.
     *
     * @return false
     */
    @Override
    public boolean hasSyntaxError() {
        return false;
    }

    /**
     * @return an empty list
     */
    @Override
    public List<String> getAllErrors() {
        return new LinkedList<>();
    }
}
