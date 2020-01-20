package com.merkrafter.representation.ast;

/****
 * A statement is any instruction that can stand for itself. Statements represent a linked list.
 *
 * @since v0.4.0
 * @author merkrafter
 ***************************************************************/
public interface Statement extends AbstractSyntaxTree {
    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * @return the next statement or null if this is the last statement
     */
    Statement getNext();

    /**
     * sets the next statement that comes after this one
     */
    void setNext(Statement next);
}
