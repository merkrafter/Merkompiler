package com.merkrafter.representation.ssa;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.VariableDescription;
import com.merkrafter.representation.ast.BinaryOperationNode;
import com.merkrafter.representation.ast.ConstantNode;
import com.merkrafter.representation.ast.VariableAccessNode;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static com.merkrafter.representation.ast.BinaryOperationNodeType.PLUS;
import static com.merkrafter.representation.ast.BinaryOperationNodeType.TIMES;
import static org.junit.jupiter.api.Assertions.*;

/****
 * @since v0.5.0
 * @author merkrafter
 ***************************************************************/
class SSATransformationTest {

    @NotNull
    private final Position p = new Position("", 0, 0); // just a dummy position

    @Test
    void additionOfTwoConstants() {
        final ConstantNode<Long> const1 = new ConstantNode<>(Type.INT, 1L, p);
        final ConstantNode<Long> const2 = new ConstantNode<>(Type.INT, 2L, p);
        final SSATransformableExpression expression = new BinaryOperationNode(const1, PLUS, const2);
        final BaseBlock baseBlock = new BaseBlock();

        expression.transformToSSA(baseBlock);

        final Instruction resultInstruction = baseBlock.getFirstInstruction();
        assertTrue(resultInstruction instanceof BinaryOperationInstruction);
        final Operand operand1 = resultInstruction.getOperands()[0];
        final Operand operand2 = resultInstruction.getOperands()[1];
        assertTrue(operand1 instanceof Constant);
        assertTrue(operand2 instanceof Constant);
        assertEquals(((Constant) operand1).getValue(), const1.getValue());
        assertEquals(((Constant) operand2).getValue(), const2.getValue());
    }

    @Test
    void additionOfConstantAndParameter() {
        final ConstantNode<Long> constNode = new ConstantNode<>(Type.INT, 1L, p);
        final VariableAccessNode varNode =
                new VariableAccessNode(new VariableDescription("var", Type.INT, 0, false), p);
        final SSATransformableExpression expression =
                new BinaryOperationNode(constNode, PLUS, varNode);
        final BaseBlock baseBlock = new BaseBlock();

        expression.transformToSSA(baseBlock);

        final Instruction resultInstruction = baseBlock.getFirstInstruction();
        assertTrue(resultInstruction instanceof BinaryOperationInstruction);
        final Operand operand1 = resultInstruction.getOperands()[0];
        final Operand operand2 = resultInstruction.getOperands()[1];
        assertTrue(operand1 instanceof Constant);
        assertTrue(operand2 instanceof ParameterOperand);
        assertEquals(((Constant) operand1).getValue(), constNode.getValue());
        assertEquals(((ParameterOperand) operand2).getVariable().getName(), varNode.getName());
    }

    @Test
    void fmaCalculation() {
        final VariableAccessNode a =
                new VariableAccessNode(new VariableDescription("a", Type.INT, 0, false), p);
        final VariableAccessNode b =
                new VariableAccessNode(new VariableDescription("b", Type.INT, 0, false), p);
        final VariableAccessNode c =
                new VariableAccessNode(new VariableDescription("c", Type.INT, 0, false), p);
        final BinaryOperationNode bxc = new BinaryOperationNode(b, TIMES, c);
        final SSATransformableExpression expression = new BinaryOperationNode(a, PLUS, bxc);
        final BaseBlock baseBlock = new BaseBlock();

        expression.transformToSSA(baseBlock);

        // first instruction should be 0: mul b, c
        final Instruction firstInstruction = baseBlock.getFirstInstruction();
        assertTrue(firstInstruction instanceof BinaryOperationInstruction);
        assertEquals(((BinaryOperationInstruction) firstInstruction).getType(), TIMES);
        final Operand operand1 = firstInstruction.getOperands()[0];
        final Operand operand2 = firstInstruction.getOperands()[1];
        assertTrue(operand1 instanceof ParameterOperand);
        assertEquals(((ParameterOperand) operand1).getVariable().getName(), b.getName());
        assertTrue(operand2 instanceof ParameterOperand);
        assertEquals(((ParameterOperand) operand2).getVariable().getName(), c.getName());

        // second instruction should be 1: add a, (0)
        assertNotNull(firstInstruction.getNext());
        final Instruction secondInstruction = firstInstruction.getNext();
        assertTrue(secondInstruction instanceof BinaryOperationInstruction);
        assertEquals(((BinaryOperationInstruction) secondInstruction).getType(), PLUS);
        final Operand operand1_ = secondInstruction.getOperands()[0];
        final Operand operand2_ = secondInstruction.getOperands()[1];
        assertTrue(operand1_ instanceof ParameterOperand);
        assertEquals(((ParameterOperand) operand1_).getVariable().getName(), a.getName());
        assertTrue(operand2_ instanceof InstructionOperand);
        assertEquals(((InstructionOperand) operand2_).getInstruction(), firstInstruction);
    }
}
