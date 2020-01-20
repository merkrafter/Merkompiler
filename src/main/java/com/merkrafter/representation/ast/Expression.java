package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

/****
 * An expression is anything that can be evaluated to a type.
 *
 * @since v0.4.0
 * @author merkrafter
 ***************************************************************/
public interface Expression extends AbstractSyntaxTree {
    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * @return the type that this expression evaluates to
     */
    Type getReturnedType();
}
