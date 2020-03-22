package com.merkrafter.lexing;

import org.jetbrains.annotations.NotNull;

/****
 * This interface can be implemented by classes that manage a position.
 *
 * @since v0.4.0
 * @author merkrafter
 ***************************************************************/
public interface Locatable {
    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------
    @NotNull
    Position getPosition();
}
