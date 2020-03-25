package com.merkrafter.representation.ssa;

import com.merkrafter.representation.VariableDescription;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.merkrafter.representation.Type.INT;
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

        final BaseBlock baseBlock = BaseBlock.getInstance();
        baseBlock.insert(instruction);
        assertSame(baseBlock.getFirstInstruction(), instruction);
        assertSame(baseBlock.getLastInstruction(), instruction);
    }

    @ExtendWith(MockitoExtension.class)
    @Test
    void insertTwoSingleInstructionsIntoEmptyBlock() {
        Mockito.lenient().when(instruction.getNext()).thenReturn(null);
        Mockito.lenient().when(instruction2.getNext()).thenReturn(null);

        final BaseBlock baseBlock = BaseBlock.getInstance();
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

        final BaseBlock baseBlock = BaseBlock.getInstance();
        baseBlock.insert(instruction);
        assertSame(baseBlock.getFirstInstruction(), instruction);
        assertSame(baseBlock.getLastInstruction(), instruction2);
    }

    @Test
    void insertPhiIntoJoinBlock() {
        final VariableDescription varDesc = new VariableDescription("var", INT, 0, false);
        final Operand operand = new Constant(1);
        final JoinBlock joinBlock = new JoinBlock();

        joinBlock.updatePhi(varDesc, operand);
        joinBlock.commitPhi();

        final Instruction instruction = joinBlock.getFirstInstruction();
        assertTrue(instruction instanceof SpecialInstruction);
        assertEquals(SpecialInstruction.Type.PHI, ((SpecialInstruction) instruction).getType());
        final Operand[] ops = instruction.getOperands();
        assertEquals(2, ops.length);
        assertEquals(operand, ops[0]);
        assertEquals(operand, ops[1]);
    }

    /**
     * After committing, the operand of a variable should be set to a phi instruction.
     */
    @Test
    void commitPhiInJoinBlock() {
        final VariableDescription varDesc = new VariableDescription("var", INT, 0, false);
        final Operand operand = new Constant(1);
        final JoinBlock joinBlock = new JoinBlock();

        joinBlock.updatePhi(varDesc, operand);
        joinBlock.commitPhi();

        assertTrue(joinBlock.getFirstInstruction() instanceof SpecialInstruction);
        final SpecialInstruction phiInstr = (SpecialInstruction) joinBlock.getFirstInstruction();
        assertEquals(SpecialInstruction.Type.PHI, phiInstr.getType());
        assertTrue(varDesc.getOperand() instanceof InstructionOperand);
        assertEquals(phiInstr, ((InstructionOperand) varDesc.getOperand()).getInstruction());
    }

    @ParameterizedTest
    @EnumSource(JoinBlock.Position.class)
    void updatePhiInJoinBlock(@NotNull final JoinBlock.Position position) {
        final VariableDescription varDesc = new VariableDescription("var", INT, 0, false);
        final Operand operand = new Constant(1);
        final Operand newOperand = new Constant(2);
        final JoinBlock joinBlock = new JoinBlock();

        joinBlock.updatePhi(varDesc, operand);
        joinBlock.setUpdatePosition(position);
        joinBlock.updatePhi(varDesc, newOperand);
        joinBlock.commitPhi();

        final Instruction instruction = joinBlock.getFirstInstruction();
        assertTrue(instruction instanceof SpecialInstruction);
        assertEquals(SpecialInstruction.Type.PHI, ((SpecialInstruction) instruction).getType());
        final Operand[] ops = instruction.getOperands();
        assertEquals(2, ops.length);
        final Operand updatedOperand = ops[position.ordinal()];
        final Operand fixedOperand = ops[1 - position.ordinal()];
        assertEquals(newOperand, updatedOperand);
        assertEquals(operand, fixedOperand);
    }

    @Test
    void resetPhiInJoinBlock() {
        final VariableDescription varDesc = new VariableDescription("var", INT, 0, false);
        final Operand operand = new Constant(1);
        final Operand newOperand = new Constant(2);
        final JoinBlock joinBlock = new JoinBlock();

        joinBlock.updatePhi(varDesc, operand);
        joinBlock.updatePhi(varDesc, newOperand);
        joinBlock.setUpdatePosition(JoinBlock.Position.SECOND);
        joinBlock.resetPhi();

        assertEquals(operand, varDesc.getOperand());
    }
}