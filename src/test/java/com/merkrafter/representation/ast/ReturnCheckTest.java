package com.merkrafter.representation.ast;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.Type;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

class ReturnCheckTest {

    private final Position p = new Position("", 0, 0); // just a dummy position

    @Mock
    Statement mockStmt;
    @Mock
    ReturnNode retNode;
    @Mock
    Expression condition;

    @ExtendWith(MockitoExtension.class)
    @Test
    void testIfElseWithoutReturnForVoid() {
        final Type expectedType = Type.VOID;
        Mockito.lenient().when(mockStmt.hasReturnType(expectedType)).thenReturn(true);
        Mockito.lenient().when(condition.getReturnedType()).thenReturn(Type.BOOLEAN);

        final IfElseNode nodeUnderTest =
                new IfElseNode(new IfNode(condition, mockStmt, p), mockStmt);
        final boolean returnedTypeCorrect = nodeUnderTest.hasReturnType(expectedType);
        assertTrue(returnedTypeCorrect);
    }

    @ExtendWith(MockitoExtension.class)
    @ParameterizedTest
    @EnumSource(Type.class)
    void testIfElseSingleType(@NotNull final Type type) {
        Mockito.lenient().when(retNode.hasReturnType(type)).thenReturn(true);
        Mockito.lenient().when(condition.getReturnedType()).thenReturn(Type.BOOLEAN);

        final IfElseNode nodeUnderTest = new IfElseNode(new IfNode(condition, retNode, p), retNode);
        final boolean returnedTypeCorrect = nodeUnderTest.hasReturnType(type);
        assertTrue(returnedTypeCorrect);
    }
}