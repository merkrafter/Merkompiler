package com.merkrafter.representation.ssa

interface Operand {
    override fun toString(): String
    fun copy(): Operand
}