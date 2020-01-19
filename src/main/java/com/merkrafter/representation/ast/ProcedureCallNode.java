package com.merkrafter.representation.ast;

import com.merkrafter.representation.ProcedureDescription;
import com.merkrafter.representation.Type;

import java.util.List;

/****
 * This AST node represents the call to a procedure or method in a JavaSST program.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ProcedureCallNode extends ASTBaseNode {
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
        return collectErrorsFrom(args);
    }
}
