package com.merkrafter.representation;

import com.merkrafter.representation.ast.Statement;
import com.merkrafter.representation.graphical.GraphicalComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/****
 * This interface represents a procedure or method in a JavaSST program.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public interface ProcedureDescription extends GraphicalComponent {
    @NotNull SymbolTable getSymbols();

    @NotNull String getName();

    @Nullable List<VariableDescription> getParamList();

    @Nullable Type getReturnType();

    @Nullable Statement getEntryPoint();

    @Override
    boolean equals(@NotNull Object o);
}
