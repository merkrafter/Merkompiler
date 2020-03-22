package com.merkrafter.representation.ssa;

import com.merkrafter.representation.ast.BinaryOperationNodeType;
import org.jetbrains.annotations.NotNull;

/****
 * This instruction has two operands and a operation type.
 *
 * @since v0.5.0
 * @author merkrafter
 ***************************************************************/
public class BinaryOperationInstruction extends Instruction {
    // ATTRIBUTES
    //==============================================================
    @NotNull
    private final BinaryOperationNodeType type;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new BinaryOperationInstruction from two Operands and a operation type.
     ***************************************************************/
    public BinaryOperationInstruction(@NotNull final Operand leftOperand,
                                      @NotNull final BinaryOperationNodeType type,
                                      @NotNull final Operand rightOperand) {
        super(new Operand[]{leftOperand, rightOperand});
        this.type = type;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------
    @NotNull
    @Override
    public String toString() {
        final Operand[] ops = getOperands();
        return String.format("%d: %s %s, %s", getId(), type.getMnemonic(), ops[0], ops[1]);
    }
}
