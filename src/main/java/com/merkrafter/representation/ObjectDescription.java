package com.merkrafter.representation;

import com.merkrafter.representation.graphical.GraphicalComponent;

import java.util.Objects;

/****
 * Describes an object and stores the name of it as a constant.
 * This class serves as a super class for more object descriptions.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public abstract class ObjectDescription implements GraphicalComponent {
    // ATTRIBUTES
    //==============================================================
    private final String name;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates an ObjectDescription with a name.
     ***************************************************************/
    public ObjectDescription(final String name) {
        this.name = name;
    }

    // GETTER
    //==============================================================

    /**
     * The name of an object is a string that contains alphanumeric characters.
     *
     * @return the name of the object represented by this object description
     */
    public String getName() {
        return name;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Two object descriptions are equal when their names are equal.
     *
     * @param other the ObjectDescription to compare this against
     * @return whether this is equal to other
     */
    @Override
    public boolean equals(final Object other) {
        // auto-generated
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final ObjectDescription that = (ObjectDescription) other;
        return Objects.equals(getName(), that.getName());
    }

}
