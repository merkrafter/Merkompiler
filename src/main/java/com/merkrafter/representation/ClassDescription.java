package com.merkrafter.representation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/****
 * This class stores information on a JavaSST class.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ClassDescription extends ObjectDescription {
    // ATTRIBUTES
    //==============================================================
    @NotNull
    private final SymbolTable symbolTable;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new ClassDescription from a name and with a symbol table from the outer scope.
     ***************************************************************/
    public ClassDescription(@NotNull final String name, @Nullable final SymbolTable outerScope) {
        super(name);
        symbolTable = new SymbolTable(outerScope);
    }

    // GETTER
    //==============================================================

    /**
     * @return table of all variables and methods that are defined in this class
     */
    @NotNull
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    // SETTER
    //==============================================================

    /**
     * @return an identifier unique in the whole AST
     */
    @Override
    public int getID() {
        return 0;
    }

    /**
     * @return dot/graphviz declarations of this component's children
     */
    @NotNull
    @Override
    public String getDotRepresentation() {
        return "";
    }
}
