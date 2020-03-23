package com.merkrafter.representation.ssa

data class InstructionOperand(val instruction: Instruction) : Operand {
    override fun toString(): String {
        return "(${instruction.id})"
    }

    override fun copy(): Operand {
        return InstructionOperand(instruction)
    }
}