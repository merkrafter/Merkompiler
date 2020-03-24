package com.merkrafter.representation.ssa;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.ActualProcedureDescription;
import com.merkrafter.representation.ProcedureDescription;
import com.merkrafter.representation.VariableDescription;
import com.merkrafter.representation.ast.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.merkrafter.representation.Type.INT;
import static com.merkrafter.representation.ast.BinaryOperationNodeType.PLUS;
import static com.merkrafter.representation.ast.BinaryOperationNodeType.TIMES;
import static org.junit.jupiter.api.Assertions.*;

/****
 * The test cases in this class test the happy path for the transformation process.
 *
 * @since v0.5.0
 * @author merkrafter
 ***************************************************************/
class SSATransformationTest {

    @NotNull
    private final Position p = new Position("", 0, 0); // just a dummy position

    @Test
    void additionOfTwoConstants() {
        final ConstantNode<Long> const1 = new ConstantNode<>(INT, 1L, p);
        final ConstantNode<Long> const2 = new ConstantNode<>(INT, 2L, p);
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
        final ConstantNode<Long> constNode = new ConstantNode<>(INT, 1L, p);
        final VariableAccessNode varNode =
                new VariableAccessNode(new VariableDescription("var", INT, 0, false), p);
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
                new VariableAccessNode(new VariableDescription("a", INT, 0, false), p);
        final VariableAccessNode b =
                new VariableAccessNode(new VariableDescription("b", INT, 0, false), p);
        final VariableAccessNode c =
                new VariableAccessNode(new VariableDescription("c", INT, 0, false), p);
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

    @Test
    void parameterlessProcedure() {
        final String PROCNAME = "proc";
        final SSATransformableExpression procCall =
                new ProcedureCallNode(new ActualProcedureDescription(INT,
                                                                     PROCNAME,
                                                                     new LinkedList<>(),
                                                                     null,
                                                                     p),
                                      new ParameterListNode(),
                                      p);
        final BaseBlock baseBlock = new BaseBlock();

        procCall.transformToSSA(baseBlock);

        final Instruction instruction = baseBlock.getFirstInstruction();
        assertTrue(instruction instanceof SpecialInstruction);
        assertEquals(SpecialInstruction.Type.DISPATCH,
                     ((SpecialInstruction) instruction).getType());

        final Operand[] ops = instruction.getOperands();
        assertEquals(3, ops.length);
        // TODO check the class name of the instruction
        assertTrue(ops[1] instanceof SymbolicOperand);
        assertEquals(PROCNAME, ((SymbolicOperand) ops[1]).getSymbol());
        assertTrue(ops[2] instanceof SymbolicOperand);
        assertEquals("this", ((SymbolicOperand) ops[2]).getSymbol());
    }

    @Test
    void singleParameterProcedure() {
        final String PROCNAME = "print";
        final long CALL_VALUE = 2L;

        final List<VariableDescription> params = new LinkedList<>();
        params.add(new VariableDescription("a", INT, 0, false));
        final List<Expression> callArgs = new LinkedList<>();
        callArgs.add(new ConstantNode<>(INT, CALL_VALUE, p));

        final SSATransformableExpression procCall =
                new ProcedureCallNode(new ActualProcedureDescription(INT,
                                                                     PROCNAME,
                                                                     params,
                                                                     null,
                                                                     p),
                                      new ParameterListNode(callArgs),
                                      p);
        final BaseBlock baseBlock = new BaseBlock();

        procCall.transformToSSA(baseBlock);

        final Instruction instruction = baseBlock.getFirstInstruction();
        assertTrue(instruction instanceof SpecialInstruction);
        assertEquals(SpecialInstruction.Type.DISPATCH,
                     ((SpecialInstruction) instruction).getType());

        final Operand[] ops = instruction.getOperands();
        assertEquals(4, ops.length);
        // TODO check the class name of the instruction
        assertTrue(ops[1] instanceof SymbolicOperand);
        assertEquals(PROCNAME, ((SymbolicOperand) ops[1]).getSymbol());
        assertTrue(ops[2] instanceof SymbolicOperand);
        assertEquals("this", ((SymbolicOperand) ops[2]).getSymbol());
        assertTrue(ops[3] instanceof Constant);
        assertEquals(CALL_VALUE, ((Constant) ops[3]).getValue());
    }

    @Test
    void expressionAsParameterToProcedure() {
        final String PROCNAME = "print";

        final List<VariableDescription> params = new LinkedList<>();
        params.add(new VariableDescription("a", INT, 0, false));
        final List<Expression> callArgs = new LinkedList<>();
        final ConstantNode<Long> const1 = new ConstantNode<>(INT, 1L, p);
        final ConstantNode<Long> const2 = new ConstantNode<>(INT, 2L, p);
        callArgs.add(new BinaryOperationNode(const1, PLUS, const2));

        final SSATransformableExpression procCall =
                new ProcedureCallNode(new ActualProcedureDescription(INT,
                                                                     PROCNAME,
                                                                     params,
                                                                     null,
                                                                     p),
                                      new ParameterListNode(callArgs),
                                      p);
        final BaseBlock baseBlock = new BaseBlock();

        procCall.transformToSSA(baseBlock);

        final Instruction firstInstruction = baseBlock.getFirstInstruction();
        assertTrue(firstInstruction instanceof BinaryOperationInstruction);
        final Operand operand1 = firstInstruction.getOperands()[0];
        final Operand operand2 = firstInstruction.getOperands()[1];
        assertTrue(operand1 instanceof Constant);
        assertTrue(operand2 instanceof Constant);
        assertEquals(((Constant) operand1).getValue(), const1.getValue());
        assertEquals(((Constant) operand2).getValue(), const2.getValue());

        assertNotNull(firstInstruction.getNext());
        final Instruction secondInstruction = firstInstruction.getNext();
        assertTrue(secondInstruction instanceof SpecialInstruction);
        assertEquals(SpecialInstruction.Type.DISPATCH,
                     ((SpecialInstruction) secondInstruction).getType());

        final Operand[] ops = secondInstruction.getOperands();
        assertEquals(4, ops.length);
        // TODO check the class name of the instruction
        assertTrue(ops[1] instanceof SymbolicOperand);
        assertEquals(PROCNAME, ((SymbolicOperand) ops[1]).getSymbol());
        assertTrue(ops[2] instanceof SymbolicOperand);
        assertEquals("this", ((SymbolicOperand) ops[2]).getSymbol());
        assertTrue(ops[3] instanceof InstructionOperand);
        assertEquals(firstInstruction, ((InstructionOperand) ops[3]).getInstruction());
    }

    @Test
    void nestedProcedureCalls() {
        final String PROCNAME = "quad";
        final long CALL_VALUE = 3L;

        final List<VariableDescription> params =
                Collections.singletonList(new VariableDescription("a", INT, 0, false));
        final ProcedureDescription proc =
                new ActualProcedureDescription(INT, PROCNAME, params, null, p);

        final List<Expression> firstCallArgs =
                Collections.singletonList(new ConstantNode<>(INT, CALL_VALUE, p));
        final ProcedureCallNode innerCall =
                new ProcedureCallNode(proc, new ParameterListNode(firstCallArgs), p);

        final SSATransformableExpression outerCall = new ProcedureCallNode(proc,
                                                                           new ParameterListNode(
                                                                                   Collections.singletonList(
                                                                                           innerCall)),
                                                                           p);
        final BaseBlock baseBlock = new BaseBlock();

        outerCall.transformToSSA(baseBlock);

        final Instruction firstInstruction = baseBlock.getFirstInstruction();
        assertTrue(firstInstruction instanceof SpecialInstruction);
        assertEquals(SpecialInstruction.Type.DISPATCH,
                     ((SpecialInstruction) firstInstruction).getType());
        final Operand[] firstOps = firstInstruction.getOperands();
        assertEquals(4, firstOps.length);
        assertTrue(firstOps[1] instanceof SymbolicOperand);
        assertEquals(proc.getName(), ((SymbolicOperand) firstOps[1]).getSymbol());
        assertTrue(firstOps[2] instanceof SymbolicOperand);
        assertEquals("this", ((SymbolicOperand) firstOps[2]).getSymbol());
        assertTrue(firstOps[3] instanceof Constant);
        assertEquals(CALL_VALUE, ((Constant) firstOps[3]).getValue());

        assertNotNull(firstInstruction.getNext());
        final Instruction secondInstruction = firstInstruction.getNext();
        assertTrue(secondInstruction instanceof SpecialInstruction);
        assertEquals(SpecialInstruction.Type.DISPATCH,
                     ((SpecialInstruction) secondInstruction).getType());

        final Operand[] ops = secondInstruction.getOperands();
        assertEquals(4, ops.length);
        // TODO check the class name of the instruction
        assertTrue(ops[1] instanceof SymbolicOperand);
        assertEquals(PROCNAME, ((SymbolicOperand) ops[1]).getSymbol());
        assertTrue(ops[2] instanceof SymbolicOperand);
        assertEquals("this", ((SymbolicOperand) ops[2]).getSymbol());
        assertTrue(ops[3] instanceof InstructionOperand);
        assertEquals(firstInstruction, ((InstructionOperand) ops[3]).getInstruction());
    }
}
