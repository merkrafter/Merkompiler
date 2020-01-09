package com.merkrafter.representation;

import java.util.List;

/****
 * This class represents a procedure or method in a JavaSST program.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ProcedureDescription extends ObjectDescription {
    // ATTRIBUTES
    //==============================================================
    private final Type returnType;
    private final List<ObjectDescription> paramList;
    private final SymbolTable symbols;


    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new ProcedureDescription with a return type, a parameter list and an outer scope.
     * This constructor does not validate whether the paramList only contains parameters (and no
     * ProcedureDescriptions, for instance) or whether the enclosingSymbolTable does only contain
     * valid objects.
     ***************************************************************/
    public ProcedureDescription(final Type returnType, final String name,
                                final List<ObjectDescription> paramList,
                                final SymbolTable enclosingSymbolTable) {
        super(name);
        this.returnType = returnType;
        this.paramList = paramList;
        this.symbols = new SymbolTable(enclosingSymbolTable);
    }

    // GETTER
    //==============================================================

    /**
     * Returns all symbols of this procedure.
     *
     * @return this class's symbol table
     */
    public SymbolTable getSymbols() {
        return symbols;
    }

    /**
     * Returns all parameters of this procedure.
     *
     * @return a list of all parameters of this procedure
     */
    public List<ObjectDescription> getParamList() {
        return paramList;
    }

    /**
     * Returns this procedure's return type.
     *
     * @return the return type of this procedure
     */
    public Type getReturnType() {
        return returnType;
    }
}
