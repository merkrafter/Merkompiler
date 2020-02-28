package com.merkrafter.representation.graphical;

import com.merkrafter.representation.ast.AbstractSyntaxTree;

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
public interface GraphicalAbstractSyntaxTree extends AbstractSyntaxTree {
    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Writing this String to a .dot file and compiling it with the dot command will draw the AST.
     *
     * @return a dot/graphviz representation of this AST
     */
    String createDotRepresentation();
}
