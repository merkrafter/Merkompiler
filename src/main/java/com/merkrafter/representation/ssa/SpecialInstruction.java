package com.merkrafter.representation.ssa;

import org.jetbrains.annotations.NotNull;

/****
 * Special instructions have a type.
 *
 * @since v0.5.0
 * @author merkrafter
 ***************************************************************/
public class SpecialInstruction extends Instruction {
    /**
     * This enum contains all types of special instructions that can occur in a JavaSST program.
     * As the instructions for these type do not differ greatly, they are modelled as an enum
     * instead of multiple sub-classes.
     */
    public enum Type {
        DISPATCH,
        RETURN
    }

    // ATTRIBUTES
    //==============================================================
    @NotNull
    private final Type type;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new special instruction
     ***************************************************************/
    public SpecialInstruction(@NotNull final Type type, @NotNull final Operand[] operands) {
        super(operands);
        this.type = type;
    }

    // GETTER
    //==============================================================
    @NotNull Type getType() {
        return type;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------
    @NotNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d: %s", getId(), type.name()));
        final Operand[] ops = getOperands();
        for (final Operand op : ops) {
            sb.append(String.format(" %s,", op));
        }
        if (ops.length > 0) {
            // remove last comma
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
