package com.merkrafter.representation.ssa

import com.merkrafter.representation.VariableDescription

class JoinBlock : BaseBlock() {
    enum class Position { FIRST, SECOND }

    private val phiTable: MutableMap<VariableDescription, SpecialInstruction> = HashMap()
    var updatePosition: Position = Position.FIRST

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
            val instruction = SpecialInstruction(SpecialInstruction.Type.PHI, arrayOf(operand, operand))
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
            insert(phiInstruction)
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
            varDesc.setOperand(instrOperands[updatePosition.ordinal])
        }
    }
}