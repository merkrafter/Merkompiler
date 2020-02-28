package com.merkrafter.representation.graphical;

import com.merkrafter.representation.ClassDescription;
import com.merkrafter.representation.ast.ClassNode;

/****
 * This class represents a graphical version of the plain ClassNode.
 *
 * @author merkrafter
 * @since v0.4.0
 ***************************************************************/
public class GraphicalClassNode extends ClassNode implements GraphicalAbstractSyntaxTree {
    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a graphical class node from a plain one.
     ***************************************************************/
    public GraphicalClassNode(final ClassNode classNode) {
        super(classNode.getClassDescription());
    }
    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * Writing this String to a .dot file and compiling it with the dot command will draw the AST.
     *
     * @return a dot/graphviz representation of this AST
     */
    @Override
    public String createDotRepresentation() {
        final ClassDescription clazz = getClassDescription();
        final StringBuilder dotRepr = new StringBuilder();
        dotRepr.append(String.format("digraph %s {", clazz.getName()));
        dotRepr.append(System.lineSeparator());
        dotRepr.append(String.format("    %s;", toString()));
        dotRepr.append(System.lineSeparator());
        dotRepr.append("}");
        return dotRepr.toString();
    }


    @Override
    public String toString() {
        return String.format("%d[shape=box,label=\"%s\"]",
                             hashCode(),
                             getClassDescription().getName());
    }
}
