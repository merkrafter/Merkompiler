package com.merkrafter.representation;

import com.merkrafter.representation.ast.Statement;

import java.util.List;

/****
 * This class represents a procedure or method in a JavaSST program.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ActualProcedureDescription extends ObjectDescription implements ProcedureDescription {
    // ATTRIBUTES
    //==============================================================
    private final Type returnType;
    private final List<VariableDescription> paramList;
    private final SymbolTable symbols;
    private Statement statements;


    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new ProcedureDescription with a return type, a parameter list and an outer scope.
     * This constructor does not validate whether the enclosingSymbolTable does only contain
     * valid objects.
     ***************************************************************/
    public ActualProcedureDescription(final Type returnType, final String name,
                                      final List<VariableDescription> paramList,
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
    @Override
    public SymbolTable getSymbols() {
        return symbols;
    }

    /**
     * Returns all parameters of this procedure.
     *
     * @return a list of all parameters of this procedure
     */
    @Override
    public List<VariableDescription> getParamList() {
        return paramList;
    }

    /**
     * Returns this procedure's return type.
     *
     * @return the return type of this procedure
     */
    @Override
    public Type getReturnType() {
        return returnType;
    }

    /**
     * @return the first statement of this procedure
     */
    @Override
    public Statement getEntryPoint() {
        return statements;
    }

    // SETTER
    //==============================================================
    public void setEntrypoint(final Statement statement) {
        this.statements = statement;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Returns whether this ProcedureDescription is equal to o.
     * For this to happen, o must be a ProcedureDescription as well. Also, both must have the same
     * name and their parameter lists must have the same types in the same order.
     * This behavior comes from a caller's view where only the name and parameters of certain types
     * are given.
     *
     * @param o ideally an other ProcedureDescription object
     * @return whether this is equal to o
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final ProcedureDescription that = (ProcedureDescription) o;
        final int numParams = getParamList().size();
        if (numParams != that.getParamList().size()) {
            return false;
        }

        /* check for each position in the parameter lists whether both contain variable
         * descriptions and whether their types match
         */
        for (int i = 0; i < numParams; i++) {
            final VariableDescription thisParam = getParamList().get(i);
            final VariableDescription thatParam = that.getParamList().get(i);
            if (thisParam != null && thatParam != null
                && thisParam.getType() != thatParam.getType()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return an identifier unique in the whole AST
     */
    @Override
    public int getID() {
        return hashCode();
    }

    /**
     * @return dot/graphviz declarations of this component
     */
    @Override
    public String getDotRepresentation() {
        final StringBuilder dotRepr = new StringBuilder();

        // define entry point
        dotRepr.append(getEntryPoint().getDotRepresentation());
        dotRepr.append(System.lineSeparator());

        // define this as a node (with method name)
        dotRepr.append(String.format("%d[shape=box,label=%s];", getID(), getName()));
        dotRepr.append(System.lineSeparator());

        // paint edge from this to entry point
        dotRepr.append(String.format("%d -> %d;", getID(), getEntryPoint().getID()));
        dotRepr.append(System.lineSeparator());

        return dotRepr.toString();
    }
}
