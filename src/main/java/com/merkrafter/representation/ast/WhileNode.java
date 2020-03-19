package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

import java.util.LinkedList;
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
    private final Expression condition;
    private final Statement loopBody;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new WhileNode from a node representing a condition and a node that is executed
     * while the condition holds.
     * The constructor does not perform a type check.
     ***************************************************************/
    public WhileNode(final Expression condition, final Statement loopBody) {
        this.condition = condition;
        this.loopBody = loopBody;
    }

    // GETTER
    //==============================================================

    /**
     * A WhileNode has a semantics error if the child nodes are null or have errors themselves.
     *
     * @return whether the tree represented by this node has a semantics error somewhere
     */
    @Override
    public boolean hasSemanticsError() {
        return condition == null || loopBody == null || condition.hasSemanticsError()
               || loopBody.hasSemanticsError();
    }

    /**
     * A WhileNode has a syntax error if the child nodes are null or have errors themselves.
     *
     * @return whether the tree represented by this node has a syntax error somewhere
     */
    @Override
    public boolean hasSyntaxError() {
        return condition == null || loopBody == null || condition.hasSyntaxError()
               || loopBody.hasSyntaxError();
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(condition, loopBody, getNext());
    }

    /**
     * Two WhileNodes are considered equal if their conditions and children are non-null and are
     * equal to each other respectively.
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof WhileNode)) {
            return false;
        }
        final WhileNode other = (WhileNode) obj;
        return condition != null && other.condition != null && loopBody != null
               && other.loopBody != null && condition.equals(other.condition) && loopBody.equals(
                other.loopBody);
    }

    /**
     * @return dot/graphviz declarations of this component's children
     */
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
