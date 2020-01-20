package com.merkrafter.representation.ast;

import java.util.LinkedList;
import java.util.List;

/****
 * This is the base node of all statement node types that an Abstract Syntax Tree
 * may be made of.
 * <p>
 * A Statement actually is a linked list of multiple statements that are executed in sequence.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public abstract class AbstractStatementNode implements Statement {
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
    public AbstractStatementNode() {
    }

    // GETTER
    //==============================================================

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

    /**
     * This is a utility method that can be used by nodes to check their children for errors.
     *
     * @param nodes child node(s) that should be tested
     * @return a list of all errors that can be found in this subtree
     */
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
