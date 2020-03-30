package com.merkrafter.representation.ssa

import com.merkrafter.representation.VariableDescription

/**
 * JoinBlocks provide methods to manage phi instructions in environments where jumps are present.
 * <p>
 * This class also works as a proxy for normal BaseBlocks. This behavior is needed in WhileNodes
 * when "normal" BaseBlocks must be converted to JoinBlocks, but can not be copied, because
 * references to the BaseBlock must stay the same as the ones from the callers.
 */
class JoinBlock(private val innerBlock: BaseBlock? = null) : BaseBlock() {
    enum class Position { FIRST, SECOND }
    enum class Environment { NONE, WHILE, IFELSE }

    private val phiTable: MutableMap<VariableDescription, Pair<Operand, SpecialInstruction>> = HashMap()
    var updatePosition: Position = Position.FIRST
    var environment: Environment = Environment.NONE

    /**
     * This method ensures that after its invocation there is a phi instruction in the cache of this
     * block that has operand at updatePosition.
     * It can then be committed via the commit() method
     */
    fun updatePhi(varDesc: VariableDescription, operand: Operand) {
        if (varDesc in phiTable) {
            val instruction = phiTable[varDesc]!!.component2()
            val instrOperands = instruction.operands
            instrOperands[updatePosition.ordinal] = operand
        } else {
            // initialization
            val prevOp: Operand = varDesc.operand
            val ops = when (updatePosition) {
                Position.FIRST -> arrayOf(operand, prevOp)
                Position.SECOND -> arrayOf(prevOp, operand)
            }
            val instruction = SpecialInstruction(SpecialInstruction.Type.PHI, ops)
            phiTable[varDesc] = Pair(prevOp, instruction)
        }
    }

    /**
     * Looks up all VariableDescriptions in the cache and generates phi instructions out of them
     * that are inserted into this block. This also sets the operands of the VariableDescriptions
     * to phi instructions.
     */
    fun commitPhi(joinBlock: JoinBlock? = null) {
        for (varDesc in phiTable.keys) {
            val phiInstruction = phiTable[varDesc]!!.component2()
            insertFirst(phiInstruction)
            val instrOp = InstructionOperand(phiInstruction)
            varDesc.operand = instrOp
            joinBlock?.updatePhi(varDesc, instrOp)

            // propagate the original operand of varDesc
            val storedPairAtOther = joinBlock?.phiTable?.get(varDesc)
            val prevOpAtThis = phiTable[varDesc]!!.first
            joinBlock?.phiTable?.set(varDesc, storedPairAtOther!!.copy(first = prevOpAtThis))
        }
    }

    fun commitPhi() = commitPhi(null)

    /**
     * Loads the operands from updatePosition from the cache for all VariableDescriptions and stores
     * them in the mentioned VariableDescriptions.
     */
    fun resetPhi() {
        for (varDesc in phiTable.keys) {
            /*val instruction = phiTable[varDesc]!!.component2()
            val instrOperands = instruction.operands
            // don't use the position that was updated right before this call
            varDesc.operand = instrOperands[1 - updatePosition.ordinal]*/
            varDesc.operand = phiTable[varDesc]!!.component1()
        }
    }

    /**
     * Uses the variable-to-phi function mappings that are stored in this JoinBlock to rename all
     * their occurrences in the given block to the respective phi functions.
     */
    fun renamePhi(block: BaseBlock) {
        var instruction: Instruction?
        for (varDesc in phiTable.keys) {
            instruction = block.firstInstruction
            while (instruction != null) {
                if (!(instruction is SpecialInstruction && instruction.type == SpecialInstruction.Type.PHI)) {
                    for ((index, operand) in instruction.operands.withIndex()) {
                        if (operand is ParameterOperand && operand.variable.equals(varDesc)) {
                            // the outmost loop ensures phiTable[varDesc] != null
                            instruction.operands[index] = InstructionOperand(phiTable[varDesc]!!.component2())
                        }
                    }
                }
                instruction = instruction.next
            }
        }
    }

    override fun getBranch(): BaseBlock? {
        if (innerBlock == null) {
            return super.getBranch()
        }
        return innerBlock.branch
    }

    override fun getFail(): BaseBlock? {
        if (innerBlock == null) {
            return super.getFail()
        }
        return innerBlock.fail
    }

    override fun getFirstInstruction(): Instruction? {
        if (innerBlock == null) {
            return super.getFirstInstruction()
        }
        return innerBlock.firstInstruction
    }

    override fun getLastInstruction(): Instruction? {
        if (innerBlock == null) {
            return super.getLastInstruction()
        }
        return innerBlock.lastInstruction
    }

    override fun insertFirst(instruction: Instruction) {
        if (innerBlock == null) {
            super.insertFirst(instruction)
            return
        }
        innerBlock.insertFirst(instruction)
    }

    override fun insert(instruction: Instruction) {
        if (innerBlock == null) {
            super.insert(instruction)
            return
        }
        innerBlock.insert(instruction)
    }

    override fun setBranch(branch: BaseBlock) {
        if (innerBlock == null) {
            super.setBranch(branch)
            return
        }
        innerBlock.setBranch(branch)
    }

    override fun setFail(fail: BaseBlock) {
        if (innerBlock == null) {
            super.setFail(fail)
            return
        }
        innerBlock.setFail(fail)
    }

    override fun equals(other: Any?): Boolean {
        if (innerBlock == null) {
            return super.equals(other)
        }
        return innerBlock == other
    }

    override fun getID(): Int {
        if (innerBlock == null) {
            return super.getID()
        }
        return innerBlock.id
    }
}