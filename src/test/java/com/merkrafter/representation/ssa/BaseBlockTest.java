package com.merkrafter.representation.ssa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

class BaseBlockTest {

    @Mock
    Instruction instruction;
    @Mock
    Instruction instruction2;

    @ExtendWith(MockitoExtension.class)
    @Test
    void insertSingleInstructionIntoEmptyBlock() {
        Mockito.lenient().when(instruction.getNext()).thenReturn(null);

        final BaseBlock baseBlock = new BaseBlock();
        baseBlock.insert(instruction);
        assertSame(baseBlock.getFirstInstruction(), instruction);
        assertSame(baseBlock.getLastInstruction(), instruction);
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void insertTwoSingleInstructionsIntoEmptyBlock() {
        Mockito.lenient().when(instruction.getNext()).thenReturn(null);
        Mockito.lenient().when(instruction2.getNext()).thenReturn(null);

        final BaseBlock baseBlock = new BaseBlock();
        baseBlock.insert(instruction);
        baseBlock.insert(instruction2);
        assertSame(baseBlock.getFirstInstruction(), instruction);
        assertSame(baseBlock.getLastInstruction(), instruction2);
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void insertTwoConnectedInstructionsIntoEmptyBlock() {
        Mockito.lenient().when(instruction.getNext()).thenReturn(instruction2);
        Mockito.lenient().when(instruction2.getNext()).thenReturn(null);

        final BaseBlock baseBlock = new BaseBlock();
        baseBlock.insert(instruction);
        assertSame(baseBlock.getFirstInstruction(), instruction);
        assertSame(baseBlock.getLastInstruction(), instruction2);
    }
}