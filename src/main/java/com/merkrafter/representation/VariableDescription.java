package com.merkrafter.representation;

import com.merkrafter.representation.ssa.Constant;
import com.merkrafter.representation.ssa.Operand;
import com.merkrafter.representation.ssa.ParameterOperand;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/****
 * This class stores data about a variable in a JavaSST program. A variable has a name, type and a
 * value. It can also have a flag set that indicates whether it can be changed.
 * <p>
 * At the moment, the value is stored as a Object type but TODO: may be changed to generics later on.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class VariableDescription extends ObjectDescription {

    // ATTRIBUTES
    //==============================================================
    @NotNull
    private final Type type;
    @NotNull
    private Object value;
    private final boolean constant;

    /**
     * The value of this variable during the SSA form transformation process
     */
    // TODO make value an Expression and transform it if needed
    @NotNull
    private Operand operand;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a VariableDescription that stores information on a variable or
     * constant.
     ***************************************************************/
    public VariableDescription(@NotNull final String name, @NotNull final Type type,
                               @NotNull final Object value, final boolean constant) {
        super(name);
        this.type = type;
        this.value = value;
        this.constant = constant;
        operand = new Constant(0L);
    }

    /****
     * Creates a VariableDescription that initializes the value with 0 and sets operand to a
     * ParameterOperand.
     ***************************************************************/
    public VariableDescription(@NotNull final String name, @NotNull final Type type,
                               final boolean constant) {
        super(name);
        this.type = type;
        this.value = 0L;
        this.constant = constant;
        operand = new ParameterOperand(this);
    }

    // GETTER
    //==============================================================
    @NotNull
    public Type getType() {
        return type;
    }

    @NotNull Object getValue() {
        return value;
    }

    public boolean isConstant() {
        return constant;
    }

    @NotNull
    public Operand getOperand() {
        return operand;
    }

    // SETTER
    //==============================================================

    /**
     * Try setting the value of this variable and return whether this was successful. It even does
     * even return true when the value was not changed.
     * This method fails when this variable is marked as a constant or the new value mismatches the
     * type of this variable.
     * <p>
     * TODO: In the future this method may throw exceptions to indicate erroneous input
     *
     * @param value the new value to set
     * @return whether this operation was successful
     */
    boolean setValue(@NotNull final Object value) {
        if (!constant) {
            if (type == Type.INT) {
                if (value instanceof Integer) {
                    this.value = value;
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public void setOperand(@NotNull final Operand operand) {
        this.operand = operand;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Two VariableDescriptions are equal if their names and types are equal. It is not necessary
     * that their values or constantnesses are equal.
     * This behavior is relevant for finding VariableDescriptions in SymbolTables to determine
     * whether a variable has been defined already.
     *
     * @param other the ObjectDescription to compare this against
     * @return whether this is equal to other
     */
    @Override
    public boolean equals(@NotNull final Object other) {
        if (this == other) {
            return true;
        }
        if (getClass() != other.getClass()) {
            return false;
        }
        final VariableDescription that = (VariableDescription) other;
        return Objects.equals(getName(), that.getName());
    }

    /**
     * @return an identifier unique in the whole AST
     */
    @Override
    public int getID() {
        return hashCode();
    }

    /**
     * @return dot/graphviz declarations of this component's children
     */
    @NotNull
    @Override
    public String getDotRepresentation() {
        return String.format("%d[label=\"%s\"];", getID(), getName());
    }
}
