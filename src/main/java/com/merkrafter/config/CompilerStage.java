package com.merkrafter.config;

/****
 * This enum represents the steps the compiler goes through in order to convert a JavaSST source
 * code file into actual byte code.
 *
 * @version v0.2.0
 * @author merkrafter
 ***************************************************************/
public enum CompilerStage {
    // CONSTANTS
    //==============================================================
    /**
     * Only scan the input and output tokens.
     */
    SCANNING,
    /**
     * Scan and parse the input and output whether this was successful.
     */
    PARSING;
}
