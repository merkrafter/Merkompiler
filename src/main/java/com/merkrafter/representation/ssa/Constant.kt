package com.merkrafter.representation.ssa

/****
 * This class stores a reference to a constant integer value (as there are only integers in JavaSST
 * so far).
 *
 * @since v0.5.0
 * @author merkrafter
 ***************************************************************/
data class Constant(val value: Long) : Operand {
    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------
    override fun toString(): String {
        return value.toString()
    }

    override fun copy(): Operand {
        return Constant(value)
    }
}
