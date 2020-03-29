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

    private val phiTable: MutableMap<VariableDescription, SpecialInstruction> = HashMap()
    var updatePosition: Position = Position.FIRST
    var environment: Environment = Environment.NONE

    /**
     * This method ensures that after its invocation there is a phi instruction in the cache of this
     * block that has operand at updatePosition.
     * It can then be committed via the commit() method
     */
    fun updatePhi(varDesc: VariableDescription, operand: Operand) {
        if (varDesc in phiTable) {
            val instruction = phiTable[varDesc]!!
            val instrOperands = instruction.operands
            instrOperands[updatePosition.ordinal] = operand
        } else {
            // initialization
            val ops = when (updatePosition) {
                Position.FIRST -> arrayOf(operand, ParameterOperand(varDesc))
                Position.SECOND -> arrayOf(ParameterOperand(varDesc), operand)
            }
            val instruction = SpecialInstruction(SpecialInstruction.Type.PHI, ops)
            phiTable[varDesc] = instruction
        }
    }

    /**
     * Looks up all VariableDescriptions in the cache and generates phi instructions out of them
     * that are inserted into this block. This also sets the operands of the VariableDescriptions
     * to phi instructions.
     */
    fun commitPhi() {
        for (varDesc in phiTable.keys) {
            val phiInstruction = phiTable[varDesc]!!
            insertFirst(phiInstruction)
            varDesc.setOperand(InstructionOperand(phiInstruction))
        }
    }

    /**
     * Loads the operands from updatePosition from the cache for all VariableDescriptions and stores
     * them in the mentioned VariableDescriptions.
     */
    fun resetPhi() {
        for (varDesc in phiTable.keys) {
            val instruction = phiTable[varDesc]!!
            val instrOperands = instruction.operands
            // don't use the position that was updated right before this call
            varDesc.setOperand(instrOperands[1 - updatePosition.ordinal])
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
                            instruction.operands[index] = InstructionOperand(phiTable[varDesc]!!)
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


}