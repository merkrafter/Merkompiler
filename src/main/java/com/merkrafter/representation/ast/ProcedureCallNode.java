package com.merkrafter.representation.ast;

import com.merkrafter.representation.ProcedureDescription;
import com.merkrafter.representation.Type;

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

}
