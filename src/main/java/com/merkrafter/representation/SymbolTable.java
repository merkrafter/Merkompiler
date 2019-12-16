package com.merkrafter.representation;

import java.util.LinkedList;
import java.util.List;

/****
 * This class serves as a set to store ObjectDescriptions.
 * The storage is organized in scopes that can be added and removed and that can be thought of as a
 * stack: When an ObjectDescription is accessed, this class searches the current scope first, then
 * the previous one etc.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class SymbolTable {
    // ATTRIBUTES
    //==============================================================
    private List<ObjectDescription> descriptions;
    private SymbolTable enclosingSymbolTable; // TODO may be final

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new empty SymbolTable without an enclosing SymbolTable.
     ***************************************************************/
    public SymbolTable() {
        this(null);
    }

    /****
     * Creates a new empty SymbolTable with the given enclosing SymbolTable.
     ***************************************************************/
    public SymbolTable(final SymbolTable enclosingSymbolTable) {
        descriptions = new LinkedList<>();
        this.enclosingSymbolTable = enclosingSymbolTable;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Inserts a new ObjectDescription to this SymbolTable and returns whether this was successful.
     *
     * @param objectDescription the ObjectDescription to insert
     * @return whether the insertion was successful
     */
    public boolean insert(final ObjectDescription objectDescription) {
        return false;
    }

    /**
     * Searches this SymbolTable for an ObjectDescription with the given prototype and returns it.
     * If no such ObjectDescription is inside this SymbolTable, null is returned.
     *
     * @param prototype an ObjectDescription that should be equal to the searched ObjectDescription
     * @return an ObjectDescription with the given prototype or null if there is no such object
     */
    public ObjectDescription find(final ObjectDescription prototype) {
        return null;
    }

    /**
     * Starts a new scope which is used to structure the ObjectDescriptions.
     */
    public void beginScope() {
    }

    /**
     * Ends the current scope which means that no new ObjectDescriptions can be added to it anymore
     * and returns whether this was successful. The operation can fail if no scope was opened
     * before.
     */
    public boolean endScope() {
        return false;
    }
}
