package com.merkrafter.representation.ast;

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

    @Mock
    Statement mockStmt;
    @Mock
    ReturnNode retNode;

    @ExtendWith(MockitoExtension.class)
    @Test
    void testIfElseWithoutReturnForVoid() {
        final Type expectedType = Type.VOID;
        Mockito.lenient().when(mockStmt.hasReturnType(expectedType)).thenReturn(true);
        final IfElseNode nodeUnderTest = new IfElseNode(new IfNode(null, mockStmt), mockStmt);
        final boolean returnedTypeCorrect = nodeUnderTest.hasReturnType(expectedType);
        assertTrue(returnedTypeCorrect);
    }

    @ExtendWith(MockitoExtension.class)
    @ParameterizedTest
    @EnumSource(Type.class)
    void testIfElseSingleType(@NotNull final Type type) {

        Mockito.lenient().when(retNode.hasReturnType(type)).thenReturn(true);
        final IfElseNode nodeUnderTest = new IfElseNode(new IfNode(null, retNode), retNode);
        final boolean returnedTypeCorrect = nodeUnderTest.hasReturnType(type);
        assertTrue(returnedTypeCorrect);
    }
}