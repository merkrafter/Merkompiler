package com.merkrafter.representation.ssa;

import com.merkrafter.representation.ast.Expression;
import org.jetbrains.annotations.NotNull;

public interface SSATransformableExpression extends Expression {
    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------
    /**
     * Extracts information needed to perform a transformation to SSA form and stores it in the
     * given BaseBlock.
     * @param baseBlock target to store the ssa form information
     */
    void transformToSSA(@NotNull final BaseBlock baseBlock);
}
