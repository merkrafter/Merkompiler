package com.merkrafter.representation.ast;

import com.merkrafter.representation.ClassDescription;

import java.util.LinkedList;
import java.util.List;

import static com.merkrafter.representation.ast.ASTBaseNode.collectErrorsFrom;

/****
 * This node represents a class definition and is kind of an entry point for the whole program.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ClassNode implements AbstractSyntaxTree {
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
}
