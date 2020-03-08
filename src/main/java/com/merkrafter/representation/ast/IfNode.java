package com.merkrafter.representation.ast;

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
    private final Expression condition;
    private final Statement ifBranch;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new IfNode from a node representing a condition and a node that is executed if the
     * condition holds.
     * The constructor does not perform a type check.
     ***************************************************************/
    public IfNode(final Expression condition, final Statement ifBranch) {
        this.condition = condition;
        this.ifBranch = ifBranch;
    }

    // GETTER
    //==============================================================

    /**
     * An IfNode has a semantics error if any of its child nodes is null or has an error itself.
     *
     * @return whether the tree represented by this node has a semantics error somewhere
     */
    @Override
    public boolean hasSemanticsError() {
        return condition == null || ifBranch == null || condition.hasSemanticsError()
               || ifBranch.hasSemanticsError();
    }

    /**
     * An IfNode has a syntax error if any of its child nodes is null or has an error itself.
     *
     * @return whether the tree represented by this node has a syntax error somewhere
     */
    @Override
    public boolean hasSyntaxError() {
        return condition == null || ifBranch == null || condition.hasSyntaxError()
               || ifBranch.hasSyntaxError();
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(condition, ifBranch);
    }

    /**
     * Two IfNodes are considered equal if their conditions and children are non-null and are
     * equal to each other respectively.
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof IfNode)) {
            return false;
        }
        final IfNode other = (IfNode) obj;
        return condition != null && other.condition != null && ifBranch != null
               && other.ifBranch != null && condition.equals(other.condition) && ifBranch.equals(
                other.ifBranch);
    }

    String getDotRepresentation() {
        final StringBuilder dotRepr = new StringBuilder();

        // define children
        dotRepr.append(String.format("%d[label=\"CONDITION\"];", condition.hashCode()));
        dotRepr.append(System.lineSeparator());
        dotRepr.append(ifBranch.getDotRepresentation());
        dotRepr.append(System.lineSeparator());

        // define this
        dotRepr.append(String.format("%d[label=\"IF\"];", hashCode()));
        dotRepr.append(System.lineSeparator());

        // define links
        dotRepr.append(String.format("%d -> %d;", hashCode(), condition.hashCode()));
        dotRepr.append(System.lineSeparator());
        dotRepr.append(String.format("%d -> %d;", hashCode(), ifBranch.getID()));
        dotRepr.append(System.lineSeparator());

        // return
        return dotRepr.toString();
    }
}
