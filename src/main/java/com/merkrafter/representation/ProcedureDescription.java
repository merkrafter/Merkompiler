package com.merkrafter.representation;

import com.merkrafter.lexing.Locatable;
import com.merkrafter.representation.ast.Statement;
import com.merkrafter.representation.graphical.GraphicalComponent;
import com.merkrafter.representation.ssa.SSATransformableProcedure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/****
 * This interface represents a procedure or method in a JavaSST program.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public interface ProcedureDescription extends GraphicalComponent, Locatable, SSATransformableProcedure {
    @NotNull SymbolTable getSymbols();

    @NotNull String getName();

    @Nullable List<VariableDescription> getParamList();

    @Nullable Type getReturnType();

    @Nullable Statement getEntryPoint();

    @Override
    boolean equals(@NotNull Object o);
}
