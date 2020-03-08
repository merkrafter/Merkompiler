package com.merkrafter.representation.ast;

import java.util.List;

/****
 * This AST node represents an if-else construct. The if branch is handled by
 * a separate IfNode instance and the else branch is simply implemented as a sequence of Statements.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class IfElseNode extends AbstractStatementNode {
    // ATTRIBUTES
    //==============================================================
    private final IfNode ifBranch;
    private final Statement elseBranch;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new IfElseNode from a node handling the if branch and a node that is executed
     * if the condition does not hold.
     * The constructor does not perform a type check.
     ***************************************************************/
    public IfElseNode(final IfNode ifBranch, final Statement elseBranch) {
        this.ifBranch = ifBranch;
        this.elseBranch = elseBranch;
    }

    // GETTER
    //==============================================================

    /**
     * An IfElseNode has a semantics error if the child nodes are null or have an error themselves.
     *
     * @return whether the tree represented by this node has a semantics error somewhere
     */
    @Override
    public boolean hasSemanticsError() {
        return ifBranch == null || elseBranch == null || ifBranch.hasSemanticsError()
               || elseBranch.hasSemanticsError();
    }

    /**
     * An IfElseNode has a syntax error if the child nodes are null or have an error themselves.
     *
     * @return whether the tree represented by this node has a syntax error somewhere
     */
    @Override
    public boolean hasSyntaxError() {
        return ifBranch == null || elseBranch == null || ifBranch.hasSyntaxError()
               || elseBranch.hasSyntaxError();
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(ifBranch, elseBranch, getNext());
    }

    /**
     * Two IfElseNodes are considered equal if their ifNodes and children are non-null and are
     * equal to each other respectively.
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof IfElseNode)) {
            return false;
        }
        final IfElseNode other = (IfElseNode) obj;
        return elseBranch != null && other.elseBranch != null && ifBranch != null
               && other.ifBranch != null && elseBranch.equals(other.elseBranch) && ifBranch.equals(
                other.ifBranch);
    }

    /**
     * @return dot/graphviz declarations of this component's children
     */
    @Override
    public String getDotRepresentation() {
        final StringBuilder dotRepr = new StringBuilder(super.getDotRepresentation());
        dotRepr.append(System.lineSeparator());

        // define children
        dotRepr.append(ifBranch.getDotRepresentation());
        dotRepr.append(System.lineSeparator());
        dotRepr.append(elseBranch.getDotRepresentation());
        dotRepr.append(System.lineSeparator());

        // define this
        dotRepr.append(String.format("%d[label=%s];", getID(), "IF_ELSE"));
        dotRepr.append(System.lineSeparator());

        // define links
        if (getNext() != null) {
            dotRepr.append(String.format("%d -> %d;", getID(), getNext().getID()));
            dotRepr.append(System.lineSeparator());
        }
        dotRepr.append(String.format("%d -> %d;", getID(), ifBranch.hashCode()));
        dotRepr.append(System.lineSeparator());
        dotRepr.append(String.format("%d -> %d;", getID(), elseBranch.getID()));
        dotRepr.append(System.lineSeparator());

        return dotRepr.toString();
    }
}
