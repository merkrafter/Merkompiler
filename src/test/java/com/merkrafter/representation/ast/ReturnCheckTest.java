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
        Mockito.lenient().when(mockStmt.isCompatibleToType(expectedType)).thenReturn(true);
        Mockito.lenient().when(condition.getReturnedType()).thenReturn(Type.BOOLEAN);

        final IfElseNode nodeUnderTest =
                new IfElseNode(new IfNode(condition, mockStmt, p), mockStmt);
        final boolean returnedTypeCorrect = nodeUnderTest.isCompatibleToType(expectedType);
        assertTrue(returnedTypeCorrect);
    }

    @ExtendWith(MockitoExtension.class)
    @ParameterizedTest
    @EnumSource(Type.class)
    void testIfElseSingleType(@NotNull final Type type) {
        Mockito.lenient().when(retNode.isCompatibleToType(type)).thenReturn(true);
        Mockito.lenient().when(condition.getReturnedType()).thenReturn(Type.BOOLEAN);

        final IfElseNode nodeUnderTest = new IfElseNode(new IfNode(condition, retNode, p), retNode);
        final boolean returnedTypeCorrect = nodeUnderTest.isCompatibleToType(type);
        assertTrue(returnedTypeCorrect);
    }

    /**
     * A WhileNode should indicate an error if a return statement inside the loop violates the
     * typing, even if following statements comply.
     */
    @ExtendWith(MockitoExtension.class)
    @Test
    void testWhileWithIncompatibleType() {
        final Type type = Type.VOID;
        Mockito.lenient().when(retNode.isCompatibleToType(type)).thenReturn(false);
        Mockito.lenient().when(retNode.hasReturnStatement()).thenReturn(true);
        Mockito.lenient().when(mockStmt.isCompatibleToType(type)).thenReturn(true);

        final WhileNode nodeUnderTest = new WhileNode(condition, retNode, p);
        nodeUnderTest.setNext(mockStmt);
        final boolean returnedTypeCorrect = nodeUnderTest.isCompatibleToType(type);
        assertFalse(returnedTypeCorrect);
    }

    /**
     * int func () {while(...){a=1;} return 1;}
     */
    @ExtendWith(MockitoExtension.class)
    @Test
    void testWhileWithoutReturnInLoopBody() {
        final Type type = Type.INT;
        Mockito.lenient().when(retNode.isCompatibleToType(type)).thenReturn(true);
        Mockito.lenient().when(mockStmt.isCompatibleToType(type)).thenReturn(false);
        Mockito.lenient().when(mockStmt.hasReturnStatement()).thenReturn(false);

        final WhileNode nodeUnderTest = new WhileNode(condition, mockStmt, p);
        nodeUnderTest.setNext(retNode);
        final boolean returnedTypeCorrect = nodeUnderTest.isCompatibleToType(type);
        assertTrue(returnedTypeCorrect);
    }

    /**
     * void func () {while(...){a=1;} return 1;}
     */
    @ExtendWith(MockitoExtension.class)
    @Test
    void testWhileWithoutReturnTypeMismatchAfterBody() {
        final Type type = Type.INT;
        Mockito.lenient().when(retNode.isCompatibleToType(type)).thenReturn(false);
        Mockito.lenient().when(mockStmt.isCompatibleToType(type)).thenReturn(true);

        final WhileNode nodeUnderTest = new WhileNode(condition, mockStmt, p);
        nodeUnderTest.setNext(retNode);
        final boolean returnedTypeCorrect = nodeUnderTest.isCompatibleToType(type);
        assertFalse(returnedTypeCorrect);
    }
}