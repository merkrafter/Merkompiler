package com.merkrafter.representation.ast;

import java.util.List;

/****
 * This interface provides a unified abstraction over both structural nodes that help during the
 * parsing process as well as actual instructional nodes that represent the program.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public interface AbstractSyntaxTree {
    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * @return whether the tree represented by this node has a semantics error somewhere
     */
    boolean hasSemanticsError();

    /**
     * @return whether the tree represented by this node has a syntax error somewhere
     */
    boolean hasSyntaxError();

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    List<String> getAllErrors();
}
