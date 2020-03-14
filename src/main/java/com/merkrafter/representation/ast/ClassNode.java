package com.merkrafter.representation.ast;

import com.merkrafter.representation.ClassDescription;
import com.merkrafter.representation.ObjectDescription;
import com.merkrafter.representation.ProcedureDescription;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.graphical.GraphicalComponent;

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
        final List<String> errors = new LinkedList<>();
        if (classDescription == null) {
            errors.add("No class was defined");
        } else {
            errors.addAll(collectErrorsFrom(classDescription.getEntryPoint()));
            errors.addAll(getErrorsFromProcedures());
            errors.addAll(getErrorsFromExpressions());
        }
        return errors;
    }

    private List<String> getErrorsFromExpressions() {
        final List<String> errors = new LinkedList<>();
        for (final ObjectDescription obj : getClassDescription().getSymbolTable()
                                                                .getDescriptions()) {
            if (obj instanceof Expression) {
                errors.addAll(((Expression) obj).getTypingErrors());
            }
        }
        return errors;
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
        final List<ObjectDescription> descriptions = getDefinedObjects();

        final StringBuilder dotRepr = new StringBuilder();
        dotRepr.append(String.format("digraph %s {", clazz.getName()));
        dotRepr.append(System.lineSeparator());

        for (final ObjectDescription objDesc : descriptions) {
            dotRepr.append(objDesc.getDotRepresentation());
            dotRepr.append(System.lineSeparator());
        }

        // add this class node
        dotRepr.append(String.format("%d[shape=box,label=\"%s\"];",
                                     getID(),
                                     getClassDescription().getName()));
        dotRepr.append(System.lineSeparator());

        // edges to children
        for (final ObjectDescription objDesc : descriptions) {
            dotRepr.append(String.format("%d -> %d;", getID(), objDesc.getID()));
            dotRepr.append(System.lineSeparator());
        }
        dotRepr.append(System.lineSeparator());

        dotRepr.append("}");
        return dotRepr.toString();
    }

    private List<String> getErrorsFromProcedures() {
        final List<String> errors = new LinkedList<>();
        for (final ObjectDescription obj : getClassDescription().getSymbolTable()
                                                                .getDescriptions()) {
            if (obj instanceof ProcedureDescription) {
                errors.addAll(findErrorsInProcedure((ProcedureDescription) obj));
            }
        }
        return errors;
    }

    private static List<String> findErrorsInProcedure(final ProcedureDescription proc) {
        final List<String> errors = new LinkedList<>();
        final Type returnType = proc.getReturnType();
        if (!proc.getEntryPoint().hasReturnType(returnType)) {
            errors.add(String.format("Return type mismatch in procedure %s", proc.getName()));
        }
        errors.addAll(collectErrorsFrom(proc.getEntryPoint()));
        errors.addAll(proc.getEntryPoint().getTypingErrors());
        return errors;
    }

    /**
     * @return the hashCode of this GraphicalClassNode
     */
    public int getID() {
        return hashCode();
    }
}
