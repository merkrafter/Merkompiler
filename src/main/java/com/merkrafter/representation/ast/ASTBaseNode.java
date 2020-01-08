package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

/****
 * This is the base node of all node types that an Abstract Syntax Tree
 * may be made of. The AST represents the program's structure.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public abstract class ASTBaseNode {
    // CONSTANTS
    //==============================================================

    // ATTRIBUTES
    //==============================================================
    /**
     * Next sequential instruction in the represented program.
     */
    private ASTBaseNode next;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new node without a next node assigned.
     ***************************************************************/
    public ASTBaseNode() {
    }

    // GETTER
    //==============================================================

    /**
     * After evaluating this node, this is the type that is propagated upwards.
     *
     * @return the return type of this node
     */
    abstract Type getReturnedType();

    /**
     * Next sequential instruction in the represented program.
     */
    public ASTBaseNode getNext() {
        return next;
    }

    // SETTER
    //==============================================================

    /**
     * Sets the instruction that comes right after the one represented by this node.
     *
     * @param next the node that represents the instruction after this node's
     */
    void setNext(final ASTBaseNode next) {
        this.next = next;
    }
}
