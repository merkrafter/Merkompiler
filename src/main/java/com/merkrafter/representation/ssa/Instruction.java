package com.merkrafter.representation.ssa;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/****
 * An SSA form instruction that the program is made of.
 * These instructions are managed as a doubly linked list
 * This class manages a static counter of all instructions created,
 * which is NOT thread-safe.
 *
 * @since v0.5.0
 * @author merkrafter
 ***************************************************************/
abstract class Instruction {
    // CONSTANTS
    //==============================================================
    private static int numberOfInstructions = 0;

    // ATTRIBUTES
    //==============================================================
    private final int id;

    @Nullable
    private Instruction next;

    @Nullable
    private Instruction prev;

    @NotNull
    private final Operand[] operands;


    // CONSTRUCTORS
    //==============================================================

    /****
     * Default constructor that sets the ID of this instruction.
     ***************************************************************/
    public Instruction(@NotNull final Operand[] operands) {
        id = numberOfInstructions++;
        this.operands = operands;
    }

    // GETTER
    //==============================================================
    int getId() {
        return id;
    }

    @Nullable Instruction getNext() {
        return next;
    }

    @Nullable Instruction getPrev() {
        return prev;
    }

    @NotNull Operand[] getOperands() {
        return operands;
    }

    // SETTER
    //==============================================================
    void setNext(@NotNull Instruction next) {
        this.next = next;
    }

    void setPrev(@NotNull Instruction prev) {
        this.prev = prev;
    }
}
