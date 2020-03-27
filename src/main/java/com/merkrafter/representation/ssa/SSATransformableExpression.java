package com.merkrafter.representation.ssa;

import com.merkrafter.representation.ast.Expression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SSATransformableExpression extends Expression {
    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------
    /**
     * Extracts information needed to perform a transformation to SSA form and stores it in the
     * given BaseBlock. After this method call, getOperand must not return null.
     * @param baseBlock target to store the ssa form information
     */
    void transformToSSA(@NotNull final BaseBlock baseBlock);

    /**
     * @return the operand that this expression was transformed to
     */
    @Nullable Operand getOperand();
}
