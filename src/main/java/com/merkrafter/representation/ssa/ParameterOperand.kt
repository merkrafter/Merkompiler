package com.merkrafter.representation.ssa

import com.merkrafter.representation.VariableDescription

data class ParameterOperand(val variable: VariableDescription) : Operand {

    override fun toString(): String {
        // TODO print the Instruction stored in the variable description
        return variable.name
    }

    override fun copy(): Operand {
        return ParameterOperand(variable)
    }
}