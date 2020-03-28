package com.merkrafter.representation;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.ast.Statement;
import com.merkrafter.representation.ssa.BaseBlock;
import com.merkrafter.representation.ssa.SSATransformableStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @NotNull
    private final Type returnType;
    @NotNull
    private final List<VariableDescription> paramList;
    @NotNull
    private final SymbolTable symbols;
    @Nullable
    private Statement statements;
    @NotNull
    private final Position position;
    @Nullable
    private BaseBlock entryBlock;
    @Nullable
    private final ClassDescription clazz;


    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new ProcedureDescription with a return type, a parameter list, an outer scope and
     * a class that this procedure is a member of.
     * This constructor does not validate whether the enclosingSymbolTable does only contain
     * valid objects.
     ***************************************************************/
    public ActualProcedureDescription(@NotNull final Type returnType, @NotNull final String name,
                                      @NotNull final List<VariableDescription> paramList,
                                      @Nullable final SymbolTable enclosingSymbolTable,
                                      @Nullable final ClassDescription clazz,
                                      @NotNull final Position position) {
        super(name);
        this.returnType = returnType;
        this.paramList = paramList;
        this.symbols = new SymbolTable(enclosingSymbolTable);
        this.clazz = clazz;
        this.position = position;
    }

    /****
     * Creates a new ProcedureDescription with a return type, a parameter list and an outer scope.
     * This constructor does not validate whether the enclosingSymbolTable does only contain
     * valid objects.
     ***************************************************************/
    public ActualProcedureDescription(@NotNull final Type returnType, @NotNull final String name,
                                      @NotNull final List<VariableDescription> paramList,
                                      @Nullable final SymbolTable enclosingSymbolTable,
                                      @NotNull final Position position) {
        this(returnType, name, paramList, enclosingSymbolTable, null, position);
    }

    // GETTER
    //==============================================================

    /**
     * Returns all symbols of this procedure.
     *
     * @return this class's symbol table
     */
    @NotNull
    @Override
    public SymbolTable getSymbols() {
        return symbols;
    }

    /**
     * Returns all parameters of this procedure.
     *
     * @return a list of all parameters of this procedure
     */
    @NotNull
    @Override
    public List<VariableDescription> getParamList() {
        return paramList;
    }

    /**
     * Returns this procedure's return type.
     *
     * @return the return type of this procedure
     */
    @NotNull
    @Override
    public Type getReturnType() {
        return returnType;
    }

    /**
     * @return the first statement of this procedure
     */
    @Nullable
    @Override
    public Statement getEntryPoint() {
        return statements;
    }

    @NotNull
    @Override
    public Position getPosition() {
        return position;
    }

    // SETTER
    //==============================================================
    public void setEntrypoint(@NotNull final Statement statement) {
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
    public boolean equals(@NotNull final Object o) {
        if (this == o) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final ProcedureDescription that = (ProcedureDescription) o;
        final int numParams = getParamList().size();
        final List<VariableDescription> paramList = that.getParamList();
        if (paramList == null || numParams != paramList.size()) {
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
    @NotNull
    @Override
    public String getDotRepresentation() {
        if(entryBlock != null){
            return getDotRepresentationForSSA();
        }
        return getDotRepresentationForAST();
    }

    @NotNull
    private String getDotRepresentationForSSA(){
        final StringBuilder dotRepr = new StringBuilder();

        // define this as a node (with method name)
        dotRepr.append(String.format("%d[shape=box,label=%s];", getID(), getName()));
        dotRepr.append(System.lineSeparator());

        assert entryBlock != null;
        dotRepr.append(entryBlock.getDotRepresentation());
        dotRepr.append(System.lineSeparator());


        dotRepr.append(String.format("%d -> %d;", getID(), entryBlock.getID()));
        dotRepr.append(System.lineSeparator());

        return dotRepr.toString();
    }

    @NotNull
    private String getDotRepresentationForAST() {
        final StringBuilder dotRepr = new StringBuilder();

        // define entry point
        if (getEntryPoint() != null) {
            dotRepr.append(getEntryPoint().getDotRepresentation());
            dotRepr.append(System.lineSeparator());

            // paint edge from this to entry point
            dotRepr.append(String.format("%d -> %d;", getID(), getEntryPoint().getID()));
            dotRepr.append(System.lineSeparator());
        }

        // define this as a node (with method name)
        dotRepr.append(String.format("%d[shape=box,label=%s];", getID(), getName()));
        dotRepr.append(System.lineSeparator());

        return dotRepr.toString();
    }

    @NotNull
    @Override
    public String getClassName() {
        if (clazz != null) {
            return clazz.getName();
        }
        return "<CLASSNAME>";
    }

    /**
     * After calling this method, getEntryBlock must not return null.
     */
    @Override
    public void transformToSSA() {
        if (statements instanceof SSATransformableStatement) {
            entryBlock = BaseBlock.getInstance();
            ((SSATransformableStatement) statements).transformToSSA(entryBlock, null);
        }
    }

}
