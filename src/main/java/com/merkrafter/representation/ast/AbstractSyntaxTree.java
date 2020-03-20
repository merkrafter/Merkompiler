package com.merkrafter.representation.ast;

import org.jetbrains.annotations.NotNull;

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
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @NotNull List<String> getAllErrors();

    /**
     * @param other the object to compare with
     * @return whether the subtree indicated by this node is equal to another tree
     */
    boolean equals(Object other);
}
