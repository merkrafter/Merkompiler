package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;
import com.merkrafter.representation.graphical.GraphicalComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/****
 * A statement is any instruction that can stand for itself. Statements represent a linked list.
 *
 * @since v0.4.0
 * @author merkrafter
 ***************************************************************/
public interface Statement extends AbstractSyntaxTree, GraphicalComponent {
    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * @return the next statement or null if this is the last statement
     */
    @Nullable Statement getNext();

    /**
     * sets the next statement that comes after this one
     */
    void setNext(@Nullable Statement next);

    /**
     * If this Statement sequence has a return statement, this method returns its type.
     * If not, null is returned.
     * @return the type that is returned by this statement sequence
     */
    boolean hasReturnType(@NotNull Type type);

    @NotNull
    List<String> getTypingErrors();
}
