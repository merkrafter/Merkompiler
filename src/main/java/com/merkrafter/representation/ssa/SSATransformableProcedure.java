package com.merkrafter.representation.ssa;

/****
 * @since v0.5.0
 * @author merkrafter
 ***************************************************************/
public interface SSATransformableProcedure {
    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * After calling this method, getEntryBlock must not return null.
     */
    void transformToSSA();

}
