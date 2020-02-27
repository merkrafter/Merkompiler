package com.merkrafter.representation;

import com.merkrafter.representation.ast.Statement;

import java.util.List;

/****
 * This interface represents a procedure or method in a JavaSST program.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public interface ProcedureDescription {
    SymbolTable getSymbols();

    List<VariableDescription> getParamList();

    Type getReturnType();

    Statement getEntryPoint();

    @Override
    boolean equals(Object o);
}
