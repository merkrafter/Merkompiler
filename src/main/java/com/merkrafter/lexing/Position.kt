package com.merkrafter.lexing

/**
 * This class holds data about the position of an compiler-relevant object as Tokens, Nodes, etc.
 */
data class Position(val filename: String, val line: Long, val column: Int) {
    override fun toString(): String {
        return "$filename($line,$column)"
    }
}