package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;
import com.merkrafter.representation.VariableDescription;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class VariableAccessNodeTest {

    /**
     * A VariableAccessNode should reflect the return type of the stored variable.
     *
     * @param type the type of the variable to test against
     */
    @ParameterizedTest
    @EnumSource(Type.class)
    void returnsTypeOfStoredVariable(final Type type) {
        final VariableDescription variableDescription =
                new VariableDescription("var", type, 5, true);
        final VariableAccessNode node = new VariableAccessNode(variableDescription);
        assertEquals(type, node.getReturnedType());
    }
}