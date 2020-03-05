package com.merkrafter.representation.ast;

import com.merkrafter.representation.ClassDescription;
import com.merkrafter.representation.ObjectDescription;
import com.merkrafter.representation.graphical.GraphicalComponent;
import com.merkrafter.representation.graphical.GraphicalObjectDescription;

import java.util.LinkedList;
import java.util.List;

import static com.merkrafter.representation.ast.AbstractStatementNode.collectErrorsFrom;

/****
 * This node represents a class definition and is kind of an entry point for the whole program.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ClassNode implements AbstractSyntaxTree, GraphicalComponent {
    // ATTRIBUTES
    //==============================================================
    private final ClassDescription classDescription;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new ClassNode from a ClassDescription.
     ***************************************************************/
    public ClassNode(final ClassDescription classDescription) {
        this.classDescription = classDescription;
    }

    // GETTER
    //==============================================================

    public ClassDescription getClassDescription() {
        return classDescription;
    }

    protected List<ObjectDescription> getDefinedObjects() {
        return getClassDescription().getSymbolTable().getDescriptions();
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * @return whether the tree represented by this node has a semantics error somewhere
     */
    @Override
    public boolean hasSemanticsError() {
        return classDescription == null || classDescription.getEntryPoint() == null
               || classDescription.getEntryPoint().hasSemanticsError();
    }

    /**
     * @return a class node has an error if the class was not defined or the class had an error
     */
    @Override
    public boolean hasSyntaxError() {
        // it is syntactically correct to not have an entry point
        return classDescription == null
               || classDescription.getEntryPoint() != null && classDescription.getEntryPoint()
                                                                              .hasSyntaxError();
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @Override
    public List<String> getAllErrors() {
        if (classDescription == null) {
            final List<String> errors = new LinkedList<>();
            errors.add("No class was defined");
            return errors;
        } else {
            return collectErrorsFrom(classDescription.getEntryPoint());
        }
    }

    /**
     * Two ClassNodes are considered equal if their class descriptions are non-null and are
     * equal to each other.
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ClassNode)) {
            return false;
        }
        final ClassNode other = (ClassNode) obj;
        return classDescription != null && other.classDescription != null
               && classDescription.equals(other.classDescription);
    }

    /**
     * Writing this String to a .dot file and compiling it with the dot command will draw the AST.
     *
     * @return a dot/graphviz representation of this AST
     */
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

        // add this class node
        dotRepr.append(String.format("%d[shape=box,label=\"%s\"];",
                                     getID(),
                                     getClassDescription().getName()));
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
    public int getID() {
        return hashCode();
    }
}
