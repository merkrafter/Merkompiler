package com.merkrafter.representation.ast;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/****
 * This class represents a return statement.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ReturnNode extends AbstractStatementNode {
    // ATTRIBUTES
    //==============================================================
    @Nullable
    private final Expression expression;
    @NotNull
    private final Position position;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new return node without an expression.
     ***************************************************************/
    public ReturnNode(@NotNull final Position position) {
        this(null, position);
    }

    /****
     * Creates a new return node with the given expression as its value.
     ***************************************************************/
    public ReturnNode(@Nullable final Expression expression, @NotNull final Position position) {
        this.expression = expression;
        this.position = position;
    }

    // GETTER
    //==============================================================

    /**
     * @return the type of the expression or VOID if this return does not have an expression
     */
    @NotNull
    public Type getReturnedType() {
        if (expression == null) {
            return Type.VOID;
        }
        return expression.getReturnedType();
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @NotNull
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(expression, getNext());
    }

    /**
     * Two ReturnNodes are considered equal if their expressions are equal to each other.
     */
    @Override
    public boolean equals(@NotNull final Object obj) {
        if (!(obj instanceof ReturnNode)) {
            return false;
        }
        final ReturnNode other = (ReturnNode) obj;
        return Objects.equals(expression, other.expression);
    }

    /**
     * @return dot/graphviz declarations of this component's children
     */
    @NotNull
    @Override
    public String getDotRepresentation() {
        final StringBuilder dotRepr = new StringBuilder(super.getDotRepresentation());
        dotRepr.append(System.lineSeparator());

        // define this
        dotRepr.append(String.format("%d[label=\"RETURN\"];", getID()));
        dotRepr.append(System.lineSeparator());

        // define child
        // define links
        if (expression != null) {
            dotRepr.append(expression.getDotRepresentation());
            dotRepr.append(System.lineSeparator());
            dotRepr.append(String.format("%d -> %d;", getID(), expression.getID()));
            dotRepr.append(System.lineSeparator());
        }
        if (getNext() != null) {
            dotRepr.append(String.format("%d -> %d;", getID(), getNext().getID()));
            dotRepr.append(System.lineSeparator());
        }

        return dotRepr.toString();
    }

    /**
     * If this Statement sequence has a return statement, this method returns its type.
     * If not, null is returned.
     *
     * @return the type that is returned by this statement sequence
     */
    @Override
    public boolean hasReturnType(@NotNull final Type type) {
        return getReturnedType().equals(type);
    }

    @NotNull
    @Override
    public List<String> getTypingErrors() {
        final List<String> errors = super.getTypingErrors();
        if (expression != null && expression.getReturnedType().equals(Type.VOID)) {
            errors.add(String.format("%s: Returning void value is not allowed",
                                     expression.getPosition()));
        }
        return errors;
    }

    @NotNull
    @Override
    public Position getPosition() {
        return position;
    }
}
