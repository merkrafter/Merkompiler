package com.merkrafter.representation.ast;

import com.merkrafter.representation.ProcedureDescription;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.VariableDescription;

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
        final List<String> errors = new LinkedList<>();
        if (procedure.getParamList() == null) {
            errors.add(String.format(
                    "Could not verify the arguments in call to unknown procedure %s",
                    procedure.getName()));
        }
        // this case should never happen because the procedure call should only be created when
        // the types of call arguments and formal parameters match, but better check this twice
        // in case something changes in the other parts of the program
        else if (args.getParameters().size() != procedure.getParamList().size()) {
            errors.add(String.format("Incorrect number of arguments in call to procedure %s",
                                     procedure.getName()));
        } else {
            for (int i = 0; i < args.getParameters().size(); i++) {
                if (!args.getParameters()
                         .get(i)
                         .getReturnedType()
                         .equals(procedure.getParamList().get(i).getType())) {
                    errors.add(String.format("Type mismatch in arg #%d in call to procedure %s",
                                             i + 1,
                                             procedure.getName()));
                }
            }
        }
        return errors;
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
