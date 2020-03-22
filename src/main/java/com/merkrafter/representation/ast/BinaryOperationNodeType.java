package com.merkrafter.representation.ast;

/****
 * This class represents all types of binary operations that exist in JavaSST.
 * There is some intersection with TokenType, but it is an extra enum for type safety reasons.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public enum BinaryOperationNodeType {
    /* due to this enum being different from TokenType, it may include bytecode information or
    something similar */
    PLUS,
    MINUS,
    TIMES,
    DIVIDE,
    EQUAL,
    LOWER_EQUAL,
    GREATER_EQUAL,
    LOWER,
    GREATER
}
