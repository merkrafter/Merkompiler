package com.merkrafter.representation.graphical;

/****
 * Classes that implement this interface can be shown to the user in some
 * graphical way. It currently supports only dot/graphviz.
 * <p>
 * A possible independent development of the "structural" AST from the graphical one is
 * desired, hence the duties are split into different packages.
 *
 * @author merkrafter
 * @since v0.4.0
 ***************************************************************/
public interface GraphicalComponent {
    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * @return an identifier unique in the whole AST
     */
    int getID();

    /**
     * @return dot/graphviz declarations of this component's children
     */
    String getDotRepresentation();
}
