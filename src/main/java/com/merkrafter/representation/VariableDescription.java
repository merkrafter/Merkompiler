package com.merkrafter.representation;

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
    private final Type type;
    private Object value;
    private final boolean constant;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a VariableDescriptor that stores information on a variable or
     * constant.
     ***************************************************************/
    public VariableDescription(final String name, final Type type, final Object value,
                               final boolean constant) {
        super(name);
        this.type = type;
        this.value = value;
        this.constant = constant;
    }

    // GETTER
    //==============================================================
    public Type getType() {
        return type;
    }

    Object getValue() {
        return value;
    }

    public boolean isConstant() {
        return constant;
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
    boolean setValue(final Object value) {
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
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final VariableDescription that = (VariableDescription) other;
        return Objects.equals(getName(), that.getName());
    }
}
