package com.merkrafter.representation.graphical;

import com.merkrafter.representation.ClassDescription;
import com.merkrafter.representation.ObjectDescription;
import com.merkrafter.representation.ast.ClassNode;

import java.util.LinkedList;
import java.util.List;

/****
 * This class represents a graphical version of the plain ClassNode.
 *
 * @author merkrafter
 * @since v0.4.0
 ***************************************************************/
public class GraphicalClassNode extends ClassNode implements GraphicalComponent {
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
    public String getDotRepresentation() {
        final ClassDescription clazz = getClassDescription();
        final List<GraphicalObjectDescription> descriptions = new LinkedList<>();
        for (final ObjectDescription obj : getDefinedObjects()) {
            descriptions.add(new GraphicalObjectDescription(obj));
        }

        final StringBuilder dotRepr = new StringBuilder();
        dotRepr.append(String.format("digraph %s {", clazz.getName()));
        dotRepr.append(System.lineSeparator());

        for (final GraphicalObjectDescription objDesc : descriptions) {
            dotRepr.append(objDesc.getDotRepresentation());
            dotRepr.append(System.lineSeparator());
        }

        dotRepr.append(getDeclaration());
        dotRepr.append(System.lineSeparator());

        // edges to children
        for (final GraphicalObjectDescription objDesc : descriptions) {
            dotRepr.append(String.format("%d -> %d;", getID(), objDesc.getID()));
            dotRepr.append(System.lineSeparator());
        }
        dotRepr.append(System.lineSeparator());

        dotRepr.append("}");
        return dotRepr.toString();
    }

    /**
     * @return the hashCode of this GraphicalClassNode
     */
    @Override
    public int getID() {
        return hashCode();
    }

    /**
     * Must include the ID.
     *
     * @return a dot/graphviz declaration of this component
     */
    private String getDeclaration() {
        return String.format("%d[shape=box,label=\"%s\"];",
                             getID(),
                             getClassDescription().getName());
    }
}
