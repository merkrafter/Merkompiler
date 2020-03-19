package com.merkrafter.representation;

import com.merkrafter.representation.ast.ParameterListNode;
import com.merkrafter.representation.ast.Statement;

import java.util.Arrays;
import java.util.List;

/****
 * This class provides a lazy evaluation of the SymbolTable's find method for ProcedureDescriptions.
 * It solves the following problem that procedures can be used before they are assigned in the
 * source code and therefore the evaluation of SymbolTable::find must be done after the whole file
 * was parsed.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ProcedureDescriptionProxy implements ProcedureDescription {
    // ATTRIBUTES
    //==============================================================
    /**
     * the symbolTable that is supposed to contain the procedure
     */
    private final SymbolTable symbolTable;
    private final String name;
    private final ParameterListNode parameters;
    private ProcedureDescription procedureDescription;
    /**
     * this is managed by the findProcedureDescription method
     */
    private Type[] signature;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new ProcedureDescriptionProxy from the information on the procedure to find and the
     * SymbolTable it is supposed to be in.
     ***************************************************************/
    public ProcedureDescriptionProxy(final String name, final ParameterListNode parameters,
                                     final SymbolTable symbolTable) {
        this.name = name;
        this.symbolTable = symbolTable;
        this.parameters = parameters;
    }

    // GETTER
    //==============================================================

    /**
     * @return the symbols of the underlying procedure if it exists or null otherwise
     */
    @Override
    public SymbolTable getSymbols() {
        findProcedureDescription();
        // it is possible that the procedure is not in symbolTable
        if (procedureDescription != null) {
            return procedureDescription.getSymbols();
        }
        return null;
    }

    @Override
    public String getName() {
        findProcedureDescription();
        // it is possible that the procedure is not in symbolTable
        if (procedureDescription != null) {
            return procedureDescription.getName();
        }
        return name;
    }

    @Override
    public List<VariableDescription> getParamList() {
        findProcedureDescription();
        // it is possible that the procedure is not in symbolTable
        if (procedureDescription != null) {
            return procedureDescription.getParamList();
        }
        return null;
    }

    @Override
    public Type getReturnType() {
        findProcedureDescription();
        // it is possible that the procedure is not in symbolTable
        if (procedureDescription != null) {
            return procedureDescription.getReturnType();
        }
        return null;
    }

    @Override
    public Statement getEntryPoint() {
        findProcedureDescription();
        if (procedureDescription != null) {
            return procedureDescription.getEntryPoint();
        }
        return null;
    }

    // METHODS
    //==============================================================
    // package-private methods
    //--------------------------------------------------------------

    /**
     * @return whether the underlying ProcedureDescription could be found in the symbolTable already
     */
    boolean resolved() {
        return procedureDescription != null;
    }

    // private methods
    //--------------------------------------------------------------

    /**
     * Evaluates the types from the ParameterListNode and attempts to find the procedure in
     * symbolTable. If it could be found, procedureDescription is not null afterwards.
     */
    void findProcedureDescription() {
        if (procedureDescription == null) {
            if (parameters.getParameters().isEmpty()) {
                signature = new Type[0];
            }
            if (signature == null) {
                signature = new Type[parameters.getParameters().size()];
                // FIXME throws NPE if the one of the parameters is a variable that was not declared
                Arrays.setAll(signature, i -> parameters.getParameters().get(i).getReturnedType());
            }
            procedureDescription = (ProcedureDescription) symbolTable.find(name, signature);
        }
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
        if (resolved()) {
            return procedureDescription.getDotRepresentation();
        }
        return String.format("%d[shape=box,label=\"extern %s\"];", getID(), name)
               + System.lineSeparator();
    }
}
