package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

import java.util.LinkedList;
import java.util.List;

/****
 * This is the base node of all node types that an Abstract Syntax Tree
 * may be made of. The AST represents the program's structure.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public abstract class ASTBaseNode implements Statement {
    // CONSTANTS
    //==============================================================

    // ATTRIBUTES
    //==============================================================
    /**
     * Next sequential instruction in the represented program.
     */
    private Statement next;

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
    public abstract Type getReturnedType();

    /**
     * Next sequential instruction in the represented program.
     */
    public Statement getNext() {
        return next;
    }

    // SETTER
    //==============================================================

    /**
     * Sets the instruction that comes right after the one represented by this node.
     *
     * @param next the node that represents the instruction after this node's
     */
    public void setNext(final Statement next) {
        this.next = next;
    }

    static List<String> collectErrorsFrom(final AbstractSyntaxTree... nodes) {
        final List<String> errors = new LinkedList<>();
        for (final AbstractSyntaxTree node : nodes) {
            if (node != null) {
                errors.addAll(node.getAllErrors());
            }
        }
        return errors;
    }
}
