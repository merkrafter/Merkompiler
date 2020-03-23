package com.merkrafter.representation.ssa;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.ast.BinaryOperationNode;
import com.merkrafter.representation.ast.ConstantNode;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static com.merkrafter.representation.ast.BinaryOperationNodeType.PLUS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
