package com.merkrafter.representation.ssa;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/****
 * A BaseBlock is a list of connected, sequential Instructions.
 * In particular, this means that it contains no branches.
 *
 * @since v0.5.0
 * @author merkrafter
 ***************************************************************/
public class BaseBlock {
    // ATTRIBUTES
    //==============================================================
    @Nullable
    private Instruction firstInstruction;

    @Nullable
    private Instruction lastInstruction;

    // GETTER
    //==============================================================
    @Nullable Instruction getFirstInstruction() {
        return firstInstruction;
    }

    @Nullable Instruction getLastInstruction() {
        return lastInstruction;
    }

    // METHODS
    //==============================================================
    // package-private methods
    //--------------------------------------------------------------

    /**
     * Inserts a list of instructions at the end of this base block.
     *
     * @param instruction a linked list of instructions to insert
     */
    void insert(@NotNull final Instruction instruction) {

        if (lastInstruction == null) {
            // this base block is empty
            firstInstruction = instruction;
        } else {
            lastInstruction.setNext(instruction);
        }

        Instruction tmp = instruction;
        while (tmp.getNext() != null) {
            tmp = tmp.getNext();
        }
        lastInstruction = tmp;

    }
}
