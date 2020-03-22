package com.merkrafter.representation.ast;

import org.jetbrains.annotations.NotNull;

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
    PLUS("add"),
    MINUS("sub"),
    TIMES("mul"),
    DIVIDE("div"),
    EQUAL("eq"),
    LOWER_EQUAL("leq"),
    GREATER_EQUAL("geq"),
    LOWER("lt"),
    GREATER("gt");

    @NotNull
    private final String mnemonic;

    BinaryOperationNodeType(@NotNull final String mnemonic) {
        this.mnemonic = mnemonic;
    }

    @NotNull
    public String getMnemonic() {
        return mnemonic;
    }
}
