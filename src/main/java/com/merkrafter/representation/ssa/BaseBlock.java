package com.merkrafter.representation.ssa;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/****
 * A BaseBlock is a list of connected, sequential Instructions.
 * In particular, this means that it contains no branches.
 * If a branch instruction occurs inside a BaseBlock, its fields branch and fail
 * point to the instructions that follow in case of success or failure respectively.
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

    @Nullable
    private BaseBlock branch;

    @Nullable
    private BaseBlock fail;

    // CONSTRUCTION
    //==============================================================
    protected BaseBlock() {
    }

    @NotNull
    public static BaseBlock getInstance() {
        return new BaseBlock();
    }

    // GETTER
    //==============================================================
    @Nullable Instruction getFirstInstruction() {
        return firstInstruction;
    }

    @Nullable Instruction getLastInstruction() {
        return lastInstruction;
    }

    /**
     * @return a block of instructions that will be executed in case of success of a previous test
     */
    @Nullable
    public BaseBlock getBranch() {
        return branch;
    }

    /**
     * @return a block of instructions that will be executed in case of failure of a previous test
     */
    @Nullable
    public BaseBlock getFail() {
        return fail;
    }

    // SETTER
    //==============================================================
    public void setBranch(@NotNull final BaseBlock branch) {
        this.branch = branch;
    }

    public void setFail(@NotNull final BaseBlock fail) {
        this.fail = fail;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Inserts a list of instructions at the end of this base block.
     *
     * @param instruction a linked list of instructions to insert
     */
    public void insert(@NotNull final Instruction instruction) {

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

    protected void insertFirst(@NotNull final Instruction instruction) {
        if (firstInstruction == null) {
            insert(instruction);
            return;
        }

        Instruction tmp = instruction;
        while (tmp.getNext() != null) {
            tmp = tmp.getNext();
        }
        tmp.setNext(firstInstruction);
        firstInstruction = tmp;

    }

}
