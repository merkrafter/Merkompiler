package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;
import com.merkrafter.representation.VariableDescription;

/****
 * This AST node represents the access to a variable from a symbol table.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class VariableAccessNode extends ASTBaseNode {
    // ATTRIBUTES
    //==============================================================
    private final VariableDescription variableDescription;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new node that represents an access to a variable.
     ***************************************************************/
    public VariableAccessNode(final VariableDescription variableDescription) {
        this.variableDescription = variableDescription;
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
        return variableDescription.getType();
    }

    /**
     * A VariableAccessNode has a semantics error if the underlying VariableDescription is null.
     *
     * @return whether the tree represented by this node has a semantics error somewhere
     */
    @Override
    public boolean hasSemanticsError() {
        return variableDescription == null;
    }

    /**
     * A VariableAccessNode can not have any syntax errors.
     *
     * @return false
     */
    @Override
    public boolean hasSyntaxError() {
        return false;
    }

}
