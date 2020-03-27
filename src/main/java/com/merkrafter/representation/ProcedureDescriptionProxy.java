package com.merkrafter.representation;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.ast.ParameterListNode;
import com.merkrafter.representation.ast.Statement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @NotNull
    private final SymbolTable symbolTable;
    @NotNull
    private final String name;
    @NotNull
    private final ParameterListNode parameters;
    @Nullable
    private ProcedureDescription procedureDescription;
    @NotNull
    private final Position position;
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
    public ProcedureDescriptionProxy(@NotNull final String name,
                                     @NotNull final ParameterListNode parameters,
                                     @NotNull final SymbolTable symbolTable,
                                     @NotNull final Position position) {
        this.name = name;
        this.symbolTable = symbolTable;
        this.parameters = parameters;
        this.position = position;
    }

    // GETTER
    //==============================================================

    /**
     * @return the symbols of the underlying procedure if it exists or null otherwise
     */
    @NotNull
    @Override
    public SymbolTable getSymbols() {
        findProcedureDescription();
        // it is possible that the procedure is not in symbolTable
        if (procedureDescription != null) {
            return procedureDescription.getSymbols();
        }
        return new SymbolTable();
    }

    @NotNull
    @Override
    public String getName() {
        findProcedureDescription();
        // it is possible that the procedure is not in symbolTable
        if (procedureDescription != null) {
            return procedureDescription.getName();
        }
        return name;
    }

    @Nullable
    @Override
    public List<VariableDescription> getParamList() {
        findProcedureDescription();
        // it is possible that the procedure is not in symbolTable
        if (procedureDescription != null) {
            return procedureDescription.getParamList();
        }
        return null;
    }

    @Nullable
    @Override
    public Type getReturnType() {
        findProcedureDescription();
        // it is possible that the procedure is not in symbolTable
        if (procedureDescription != null) {
            return procedureDescription.getReturnType();
        }
        return null;
    }

    @Nullable
    @Override
    public Statement getEntryPoint() {
        findProcedureDescription();
        if (procedureDescription != null) {
            return procedureDescription.getEntryPoint();
        }
        return null;
    }

    @NotNull
    @Override
    public Position getPosition() {
        return position;
    }

    // METHODS
    //==============================================================
    // package-private methods
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
    @NotNull
    @Override
    public String getDotRepresentation() {
        if (procedureDescription != null) {
            return procedureDescription.getDotRepresentation();
        }
        return String.format("%d[shape=box,label=\"extern %s\"];", getID(), name)
               + System.lineSeparator();
    }

    /**
     * After calling this method, getEntryBlock must not return null.
     */
    @Override
    public void transformToSSA() {
        findProcedureDescription();
        if (procedureDescription != null) {
            procedureDescription.transformToSSA();
        }
    }

}
