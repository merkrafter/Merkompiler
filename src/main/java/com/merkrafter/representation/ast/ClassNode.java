package com.merkrafter.representation.ast;

import com.merkrafter.representation.ClassDescription;
import com.merkrafter.representation.ObjectDescription;
import com.merkrafter.representation.ProcedureDescription;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.graphical.GraphicalComponent;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    private final ClassDescription classDescription;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new ClassNode from a ClassDescription.
     ***************************************************************/
    public ClassNode(@NotNull final ClassDescription classDescription) {
        this.classDescription = classDescription;
    }

    // GETTER
    //==============================================================

    @NotNull
    public ClassDescription getClassDescription() {
        return classDescription;
    }

    @NotNull
    protected List<ObjectDescription> getDefinedObjects() {
        return getClassDescription().getSymbolTable().getDescriptions();
    }

    /**
     * @return the hashCode of this GraphicalClassNode
     */
    @Override
    public int getID() {
        return hashCode();
    }

    // METHODS
    //==============================================================
    // public methods
    //--------------------------------------------------------------

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @NotNull
    @Override
    public List<String> getAllErrors() {
        final List<String> errors = new LinkedList<>();
        errors.addAll(collectErrorsFrom(classDescription.getEntryPoint()));
        errors.addAll(getErrorsFromProcedures());
        errors.addAll(getErrorsFromExpressions());
        return errors;
    }

    @NotNull
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
    public boolean equals(@NotNull final Object obj) {
        if (!(obj instanceof ClassNode)) {
            return false;
        }
        final ClassNode other = (ClassNode) obj;
        return classDescription.equals(other.classDescription);
    }

    /**
     * Writing this String to a .dot file and compiling it with the dot command will draw the AST.
     *
     * @return a dot/graphviz representation of this AST
     */
    @NotNull
    @Override
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

    @NotNull
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

    @NotNull
    private static List<String> findErrorsInProcedure(@NotNull final ProcedureDescription proc) {
        final List<String> errors = new LinkedList<>();
        final Type returnType = proc.getReturnType();
        final Statement stmt = proc.getEntryPoint();
        if (stmt == null || returnType == null || !stmt.hasReturnType(returnType)) {
            errors.add(String.format("Return type mismatch in procedure %s", proc.getName()));
        }
        errors.addAll(collectErrorsFrom(proc.getEntryPoint()));
        errors.addAll(proc.getEntryPoint().getTypingErrors());
        return errors;
    }

}
