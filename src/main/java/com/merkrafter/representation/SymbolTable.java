package com.merkrafter.representation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @NotNull
    private final List<ObjectDescription> descriptions;
    @Nullable
    private final SymbolTable enclosingSymbolTable;

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
    public SymbolTable(@Nullable final SymbolTable enclosingSymbolTable) {
        descriptions = new LinkedList<>();
        this.enclosingSymbolTable = enclosingSymbolTable;
    }

    // GETTER
    //==============================================================
    @NotNull
    public List<ObjectDescription> getDescriptions() {
        return descriptions;
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
    public boolean insert(@NotNull final ObjectDescription objectDescription) {
        for (final ObjectDescription storedObjDesc : descriptions) {
            if (storedObjDesc.equals(objectDescription)) {
                return false;
            }
        }
        // add this element to the end of the description list
        descriptions.add(objectDescription);
        return true;
    }

    /**
     * Searches this SymbolTable for an ObjectDescription with the given prototype and returns it.
     * If no such ObjectDescription is inside this SymbolTable, null is returned.
     * If this symbol table has an enclosing table, it is searched as well.
     *
     * @param prototype an ObjectDescription that should be equal to the searched ObjectDescription
     * @return an ObjectDescription with the given prototype or null if there is no such object
     */
    @Nullable
    public ObjectDescription find(@NotNull final ObjectDescription prototype) {
        for (final ObjectDescription storedObjDesc : descriptions) {
            if (storedObjDesc.equals(prototype)) {
                return storedObjDesc;
            }
        }
        if (enclosingSymbolTable == null) {
            return null;
        } else {
            return enclosingSymbolTable.find(prototype);
        }
    }

    /**
     * Searches this SymbolTable for an ObjectDescription with the given prototype and returns it.
     * If no such ObjectDescription is inside this SymbolTable, null is returned.
     * If this symbol table has an enclosing table, it is searched as well.
     *
     * @param name the name of the ObjectDescription to find
     * @param signature if a procedure is searched, then the signature can be passed here; otherwise set it to null
     * @return an ObjectDescription with the given prototype or null if there is no such object
     */
    @Nullable
    public ObjectDescription find(@NotNull final String name, @Nullable final Type... signature) {
        ObjectDescription prototype;
        if (signature == null) { // if signature.length == 0 it is a parameterless procedure
            // this is a variable; only name is really relevant
            prototype = new VariableDescription(name, Type.VOID, 0, false);
        } else {
            // this is a procedure; only name and list of parameter types are relevant
            final List<VariableDescription> paramList = new LinkedList<>();
            for (final Type type : signature) {
                // for the parameters only the types are relevant
                if (type != null) {
                    paramList.add(new VariableDescription("", type, 0, false));
                }
            }
            prototype = new ActualProcedureDescription(Type.VOID, name, paramList, null);
        }
        return find(prototype);
    }
}
