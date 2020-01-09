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

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new node that represents a procedure call.
     ***************************************************************/
    public ProcedureCallNode(final ProcedureDescription procedure) {
        this.procedure = procedure;
    }

    // GETTER
    //==============================================================

    /**
     * After evaluating this node, this is the type that is propagated upwards.
     *
     * @return the return type of this node
     */
    @Override
    Type getReturnedType() {
        return procedure.getReturnType();
    }
}
