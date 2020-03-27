package com.merkrafter.representation.ssa

class SymbolicOperand(val symbol: String) : Operand {
    override fun toString(): String {
        return "'$symbol'"
    }

    override fun copy(): Operand {
        return SymbolicOperand(symbol)
    }
}