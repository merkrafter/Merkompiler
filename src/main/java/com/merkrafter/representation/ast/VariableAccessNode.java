package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;
import com.merkrafter.representation.VariableDescription;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/****
 * This AST node represents the access to a variable from a symbol table.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class VariableAccessNode implements Expression {
    // ATTRIBUTES
    //==============================================================
    @NotNull
    private final VariableDescription variableDescription;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new node that represents an access to a variable.
     ***************************************************************/
    public VariableAccessNode(@NotNull final VariableDescription variableDescription) {
        this.variableDescription = variableDescription;
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
        return variableDescription.getType();
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
     * Returns whether the underlying variable is a constant.
     */
    boolean isConstant() {
        return variableDescription.isConstant();
    }

    public String getName() {
        return variableDescription.getName();
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
     * Two VariableAccessNodes are considered equal if their variable descriptions are non-null and
     * equal to each other.
     */
    @Override
    public boolean equals(@NotNull final Object obj) {
        if (!(obj instanceof VariableAccessNode)) {
            return false;
        }
        final VariableAccessNode other = (VariableAccessNode) obj;
        return variableDescription.equals(other.variableDescription);
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
        return String.format("%d[label=\"VAR%s%s\"];", getID(), System.lineSeparator(), getName());
    }
}
