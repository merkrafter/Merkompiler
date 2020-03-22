package com.merkrafter.representation.ast;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.VariableDescription;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class VariableAccessNodeTest {

    private final Position p = new Position("", 0, 0); // just a dummy position

    /**
     * A VariableAccessNode should reflect the return type of the stored variable.
     *
     * @param type the type of the variable to test against
     */
    @ParameterizedTest
    @EnumSource(Type.class)
    void returnsTypeOfStoredVariable(@NotNull final Type type) {
        final VariableDescription variableDescription =
                new VariableDescription("var", type, 5, true);
        final VariableAccessNode node = new VariableAccessNode(variableDescription, p);
        assertEquals(type, node.getReturnedType());
    }
}