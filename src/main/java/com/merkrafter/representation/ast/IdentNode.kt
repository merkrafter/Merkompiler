package com.merkrafter.representation.ast

import com.merkrafter.lexing.Position
import org.jetbrains.annotations.NotNull
import java.util.LinkedList

data class IdentNode(val identifier: String, val position: Position) : AbstractSyntaxTree {
    /**
     * @return an empty list
     */
    override fun getAllErrors(): @NotNull MutableList<String> {
        return LinkedList<String>()
    }
}