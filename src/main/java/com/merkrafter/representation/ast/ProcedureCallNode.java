package com.merkrafter.representation.ast;

import com.merkrafter.representation.ProcedureDescription;
import com.merkrafter.representation.Type;

import java.util.LinkedList;
import java.util.List;

/****
 * This AST node represents the call to a procedure or method in a JavaSST program.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ProcedureCallNode extends AbstractStatementNode implements Expression {
    // ATTRIBUTES
    //==============================================================
    private final ProcedureDescription procedure;

    private final ParameterListNode args;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new node that represents a procedure call with the
     * given arguments.
     ***************************************************************/
    public ProcedureCallNode(final ProcedureDescription procedure, final ParameterListNode args) {
        this.procedure = procedure;
        this.args = args;
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
        return procedure.getReturnType();
    }

    @Override
    public List<String> getTypingErrors() {
        return new LinkedList<>();
    }

    /**
     * @return the arguments of this procedure call
     */
    ParameterListNode getArgs() {
        return args;
    }

    /**
     * A ProcedureCallNode has a semantics error if the underlying ProcedureDescription is null.
     *
     * @return whether the tree represented by this node has a semantics error somewhere
     */
    @Override
    public boolean hasSemanticsError() {
        return procedure == null;
    }

    /**
     * A ProcedureCallNode has a syntax error if the underlying ProcedureDescription is null.
     *
     * @return whether the tree represented by this node has a syntax error somewhere
     */
    @Override
    public boolean hasSyntaxError() {
        return procedure == null;
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(args, getNext());
    }

    /**
     * Two ProcedureCallNodes are considered equal if their procedures and args are non-null and are
     * equal to each other respectively.
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ProcedureCallNode)) {
            return false;
        }
        final ProcedureCallNode other = (ProcedureCallNode) obj;
        return procedure != null && other.procedure != null && args != null && other.args != null
               && procedure.equals(other.procedure) && args.equals(other.args);
    }

    @Override
    public int getID() {
        return procedure.getID();
    }

    /**
     * @return dot/graphviz declarations of this component's children
     */
    @Override
    public String getDotRepresentation() {
        final StringBuilder dotRepr = new StringBuilder(super.getDotRepresentation());
        dotRepr.append(System.lineSeparator());

        // define this node
        dotRepr.append(String.format("%d[label=\"%s\"];", procedure.getID(), procedure.getName()));

        // define links to next
        if (getNext() != null) {
            dotRepr.append(String.format("%d -> %d;", getID(), getNext().getID()));
            dotRepr.append(System.lineSeparator());
        }

        return dotRepr.toString();
    }
}
