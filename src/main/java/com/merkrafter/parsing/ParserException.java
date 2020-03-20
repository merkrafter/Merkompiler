package com.merkrafter.parsing;

import org.jetbrains.annotations.NotNull;

/****
 * This class indicates an error during the parsing.
 * It is intended to supersede the ErrorNode system.
 *
 * @since v0.4.0
 * @author merkrafter
 ***************************************************************/
public class ParserException extends Exception {
    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new ParserException by setting a message.
     ***************************************************************/
    public ParserException(@NotNull final String message) {
        super(message);
    }
}
