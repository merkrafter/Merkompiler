
package com.merkrafter.representation.ssa;

import org.jetbrains.annotations.NotNull;

/****
 * This interface can be used to mark statements that can be transformed to SSA form.
 *
 * @since v0.5.0
 * @author merkrafter
 ***************************************************************/
public interface SSATransformableStatement {
    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------
    void transformToSSA(@NotNull BaseBlock baseBlock);
}
