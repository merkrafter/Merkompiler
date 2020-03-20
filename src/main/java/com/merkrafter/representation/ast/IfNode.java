package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.merkrafter.representation.ast.AbstractStatementNode.collectErrorsFrom;

/****
 * This AST node represents an if statement.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class IfNode implements AbstractSyntaxTree {
    // ATTRIBUTES
    //==============================================================
    @NotNull
    private final Expression condition;
    @NotNull
    private final Statement ifBranch;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new IfNode from a node representing a condition and a node that is executed if the
     * condition holds.
     * The constructor does not perform a type check.
     ***************************************************************/
    public IfNode(@NotNull final Expression condition, @NotNull final Statement ifBranch) {
        this.condition = condition;
        this.ifBranch = ifBranch;
    }

    // GETTER
    //==============================================================

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @NotNull
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(condition, ifBranch);
    }

    /**
     * Two IfNodes are considered equal if their conditions and children are non-null and are
     * equal to each other respectively.
     */
    @Override
    public boolean equals(@NotNull final Object obj) {
        if (!(obj instanceof IfNode)) {
            return false;
        }
        final IfNode other = (IfNode) obj;
        return condition.equals(other.condition) && ifBranch.equals(other.ifBranch);
    }

    @NotNull String getDotRepresentation() {
        final StringBuilder dotRepr = new StringBuilder();

        // define children
        dotRepr.append(condition.getDotRepresentation());
        dotRepr.append(System.lineSeparator());
        dotRepr.append(ifBranch.getDotRepresentation());
        dotRepr.append(System.lineSeparator());

        // define this
        dotRepr.append(String.format("%d[label=\"IF\"];", hashCode()));
        dotRepr.append(System.lineSeparator());

        // define links
        dotRepr.append(String.format("%d -> %d;", hashCode(), condition.getID()));
        dotRepr.append(System.lineSeparator());
        dotRepr.append(String.format("%d -> %d;", hashCode(), ifBranch.getID()));
        dotRepr.append(System.lineSeparator());

        // return
        return dotRepr.toString();
    }

    /**
     * @return the type of the if branch
     */
    boolean hasReturnType(@NotNull final Type type) {
        return ifBranch.hasReturnType(type);
    }

    @NotNull
    public List<String> getTypingErrors() {
        final List<String> errors = condition.getTypingErrors();
        if (!condition.getReturnedType().equals(Type.BOOLEAN)) {
            errors.add("Condition does not evaluate to boolean in if statement");
        }
        errors.addAll(ifBranch.getTypingErrors());
        return errors;
    }
}
