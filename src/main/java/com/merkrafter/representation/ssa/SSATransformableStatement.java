package com.merkrafter.representation.ssa;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    void transformToSSA(@NotNull final BaseBlock baseBlock, @Nullable final JoinBlock joinBlock);
}
