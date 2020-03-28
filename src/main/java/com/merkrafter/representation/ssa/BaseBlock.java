package com.merkrafter.representation.ssa;

import com.merkrafter.representation.graphical.GraphicalComponent;
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
public class BaseBlock implements GraphicalComponent {
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

    /**
     * To avoid loops during printing blocks of while loops.
     */
    private boolean drawn;

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

    /**
     * @return an identifier unique in the whole graphic
     */
    @Override
    public int getID() {
        return hashCode();
    }

    /**
     * @return dot/graphviz declarations of this
     */
    @NotNull
    @Override
    public String getDotRepresentation() {
        if (drawn) {
            return "";
        }
        final StringBuilder dotRepr = new StringBuilder();

        // define statements
        final StringBuilder instrStrings = new StringBuilder();
        Instruction instr = firstInstruction;
        if (instr == null) {
            instrStrings.append("<empty>");
        }
        while (instr != null) {
            instrStrings.append(instr.toString());
            instrStrings.append(System.lineSeparator());
            instr = instr.getNext();
        }

        // define this
        dotRepr.append(String.format("%d[shape=box,label=\"%s\"];", getID(), instrStrings));
        dotRepr.append(System.lineSeparator());
        drawn = true;

        if (branch != null) {
            dotRepr.append(String.format("%d -> %d[label=branch];", getID(), branch.getID()));
            dotRepr.append(System.lineSeparator());
            dotRepr.append(branch.getDotRepresentation());
        }

        if (fail != null) {
            dotRepr.append(String.format("%d -> %d[label=fail];", getID(), fail.getID()));
            dotRepr.append(System.lineSeparator());
            dotRepr.append(fail.getDotRepresentation());
        }

        return dotRepr.toString();
    }
}
