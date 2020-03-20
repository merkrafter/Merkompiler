package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/****
 * This AST node represents a while construct. It is very similar to the IfNode but can not
 * be interchanged with it as there is not while-else construct, for instance.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class WhileNode extends AbstractStatementNode {
    // ATTRIBUTES
    //==============================================================
    @NotNull
    private final Expression condition;
    @NotNull
    private final Statement loopBody;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new WhileNode from a node representing a condition and a node that is executed
     * while the condition holds.
     * The constructor does not perform a type check.
     ***************************************************************/
    public WhileNode(@NotNull final Expression condition, @NotNull final Statement loopBody) {
        this.condition = condition;
        this.loopBody = loopBody;
    }

    // GETTER
    //==============================================================

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @NotNull
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(condition, loopBody, getNext());
    }

    /**
     * Two WhileNodes are considered equal if their conditions and children are non-null and are
     * equal to each other respectively.
     */
    @Override
    public boolean equals(@NotNull final Object obj) {
        if (!(obj instanceof WhileNode)) {
            return false;
        }
        final WhileNode other = (WhileNode) obj;
        return condition.equals(other.condition) && loopBody.equals(other.loopBody);
    }

    /**
     * @return dot/graphviz declarations of this component's children
     */
    @NotNull
    @Override
    public String getDotRepresentation() {
        final StringBuilder dotRepr = new StringBuilder(super.getDotRepresentation());
        dotRepr.append(System.lineSeparator());

        // define children
        dotRepr.append(condition.getDotRepresentation());
        dotRepr.append(System.lineSeparator());
        dotRepr.append(loopBody.getDotRepresentation());
        dotRepr.append(System.lineSeparator());

        // define this
        dotRepr.append(String.format("%d[label=%s];", getID(), "WHILE"));
        dotRepr.append(System.lineSeparator());

        // define links
        dotRepr.append(String.format("%d -> %d;", getID(), condition.getID()));
        dotRepr.append(System.lineSeparator());
        if (getNext() != null) {
            dotRepr.append(String.format("%d -> %d;", getID(), getNext().getID()));
            dotRepr.append(System.lineSeparator());
        }
        dotRepr.append(String.format("%d -> %d;", getID(), loopBody.getID()));
        dotRepr.append(System.lineSeparator());

        // return
        return dotRepr.toString();
    }

    @NotNull
    @Override
    public List<String> getTypingErrors() {
        final List<String> errors = super.getTypingErrors();
        errors.addAll(condition.getTypingErrors());
        if (!condition.getReturnedType().equals(Type.BOOLEAN)) {
            errors.add("Condition does not evaluate to boolean in while loop");
        }
        errors.addAll(loopBody.getTypingErrors());
        return errors;
    }
}
