package com.merkrafter.representation.graphical;

import com.merkrafter.representation.ObjectDescription;
import com.merkrafter.representation.ProcedureDescription;

/****
 * This class represents a graphical version of the plain ObjectDescription.
 *
 * @author merkrafter
 * @since v0.4.0
 ***************************************************************/
public class GraphicalObjectDescription implements GraphicalComponent {
    // ATTRIBUTES
    //==============================================================
    private final ObjectDescription objectDescription;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Default constructor
     ***************************************************************/
    public GraphicalObjectDescription(final ObjectDescription objectDescription) {
        this.objectDescription = objectDescription;
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * @return an identifier unique in the whole AST
     */
    @Override
    public int getID() {
        return hashCode();
    }

    /**
     * @return the dot/graphviz declaration of this component
     */
    @Override
    public String getDotRepresentation() {
        if (objectDescription instanceof ProcedureDescription) {
            return String.format("%d[shape=box, label=\"%s\"];",
                                 getID(),
                                 objectDescription.getName());
        }
        return String.format("%d[label=\"%s\"];", getID(), objectDescription.getName());
    }

}
