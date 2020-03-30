package com.merkrafter.representation.ssa;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.ActualProcedureDescription;
import com.merkrafter.representation.ProcedureDescription;
import com.merkrafter.representation.VariableDescription;
import com.merkrafter.representation.ast.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.merkrafter.representation.Type.INT;
import static com.merkrafter.representation.ast.BinaryOperationNodeType.*;
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
        /*
         * code:
         * 1 + 1;
         * expected result:
         * 0: add 1, 1
         */
        final ConstantNode<Long> const1 = new ConstantNode<>(INT, 1L, p);
        final ConstantNode<Long> const2 = new ConstantNode<>(INT, 2L, p);
        final SSATransformableExpression expression = new BinaryOperationNode(const1, PLUS, const2);
        final BaseBlock baseBlock = BaseBlock.getInstance();

        expression.transformToSSA(baseBlock);

        final Instruction resultInstruction = baseBlock.getFirstInstruction();
        assertIsBinOpInstructionOfConstants(resultInstruction,
                                            PLUS,
                                            const1.getValue(),
                                            const2.getValue());
    }

    @Test
    void additionOfConstantAndParameter() {
        /*
         * code:
         * var + 1;
         * expected result:
         * 0: add 1, var
         */
        final ConstantNode<Long> constNode = new ConstantNode<>(INT, 1L, p);
        final VariableAccessNode varNode =
                new VariableAccessNode(new VariableDescription("var", INT, false), p);
        final SSATransformableExpression expression =
                new BinaryOperationNode(constNode, PLUS, varNode);
        final BaseBlock baseBlock = BaseBlock.getInstance();

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
        /*
         * code:
         * a + b * c
         * expected result:
         * 0: mul b, c
         * 1: add a, (0)
         */
        final VariableAccessNode a =
                new VariableAccessNode(new VariableDescription("a", INT, false), p);
        final VariableAccessNode b =
                new VariableAccessNode(new VariableDescription("b", INT, false), p);
        final VariableAccessNode c =
                new VariableAccessNode(new VariableDescription("c", INT, false), p);
        final BinaryOperationNode bxc = new BinaryOperationNode(b, TIMES, c);
        final SSATransformableExpression expression = new BinaryOperationNode(a, PLUS, bxc);
        final BaseBlock baseBlock = BaseBlock.getInstance();

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
        /*
         * code:
         * proc();
         * expected result:
         * 0: DISPATCH CLASS, proc, this
         */
        final String PROCNAME = "proc";
        final SSATransformableExpression procCall =
                new ProcedureCallNode(new ActualProcedureDescription(INT,
                                                                     PROCNAME,
                                                                     new LinkedList<>(),
                                                                     null,
                                                                     p),
                                      new ParameterListNode(),
                                      p);
        final BaseBlock baseBlock = BaseBlock.getInstance();

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
        /*
         * code:
         * print(2);
         * expected result:
         * 0: DISPATCH CLASS, print, this, 2
         */
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
        final BaseBlock baseBlock = BaseBlock.getInstance();

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
        /*
         * code:
         * print(1+2);
         * expected result:
         * 0: add 1, 2
         * 1: DISPATCH CLASS, print, this, (0)
         */
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
        final BaseBlock baseBlock = BaseBlock.getInstance();

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
        /*
         * code:
         * quad(quad(3));
         * expected result:
         * 0: DISPATCH CLASS, quad, this, 3
         * 1: DISPATCH CLASS, quad, this, (0)
         */
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
        final BaseBlock baseBlock = BaseBlock.getInstance();

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

    @Test
    void returnOfTwoAssignedVariables() {
        /*
         * code:
         * a = 1;
         * b = a;
         * return a + b;
         * expected result:
         * 0: add 1, 1
         * 1: RETURN (0)
         */
        final ConstantNode<Long> const1 = new ConstantNode<>(INT, 1L, p);
        final VariableAccessNode a =
                new VariableAccessNode(new VariableDescription("a", INT, 0, false), p);
        final AssignmentNode aEq1 = new AssignmentNode(a, const1);
        final VariableAccessNode b =
                new VariableAccessNode(new VariableDescription("b", INT, 0, false), p);
        final AssignmentNode bEqa = new AssignmentNode(b, a);
        aEq1.setNext(bEqa);
        final SSATransformableExpression expression = new BinaryOperationNode(a, PLUS, b);
        final ReturnNode returnNode = new ReturnNode(expression, p);
        bEqa.setNext(returnNode);
        final BaseBlock baseBlock = BaseBlock.getInstance();

        aEq1.transformToSSA(baseBlock, null);
        expression.transformToSSA(baseBlock);

        final Instruction firstInstruction = baseBlock.getFirstInstruction();
        assertNotNull(firstInstruction);
        assertIsBinOpInstructionOfConstants(firstInstruction,
                                            PLUS,
                                            const1.getValue(),
                                            const1.getValue());

        final Instruction secondInstruction = firstInstruction.getNext();
        assertNotNull(secondInstruction);
        assertTrue(secondInstruction instanceof SpecialInstruction);
        assertEquals(SpecialInstruction.Type.RETURN,
                     ((SpecialInstruction) secondInstruction).getType());
        final Operand op = secondInstruction.getOperands()[0];
        assertTrue(op instanceof InstructionOperand);
        assertEquals(firstInstruction, ((InstructionOperand) op).getInstruction());
    }

    @Test
    void assignmentsInBranch() {
        /*
         * code:
         * if(1==1){a=1+2;}else{a=1-2;}
         * print(a);
         * expected result:
         * 0: eq 1, 1                           [in baseBlock]
         * 1: add 1, 2                          [in thenBlock]
         * 2: sub 1, 2                          [in elseBlock]
         * 3: phi (1), (2)                      [in joinBlock]
         * 4: DISPATCH CLASS, print, this, (3)  [in lastBlock]
         */

        final ConstantNode<Long> const1 = new ConstantNode<>(INT, 1L, p);
        final ConstantNode<Long> const2 = new ConstantNode<>(INT, 2L, p);
        final VariableAccessNode a =
                new VariableAccessNode(new VariableDescription("a", INT, 0, false), p);
        final AssignmentNode aEq1 =
                new AssignmentNode(a, new BinaryOperationNode(const1, PLUS, const2));
        final AssignmentNode aEq2 =
                new AssignmentNode(a, new BinaryOperationNode(const1, MINUS, const2));
        final BinaryOperationNode condition = new BinaryOperationNode(const1, EQUAL, const1);
        final IfElseNode ifStatement = new IfElseNode(new IfNode(condition, aEq1, p), aEq2);
        final List<VariableDescription> params =
                Collections.singletonList(new VariableDescription("a", INT, 0, false));
        final List<Expression> callArgs = Collections.singletonList(a);

        final ProcedureCallNode procCall =
                new ProcedureCallNode(new ActualProcedureDescription(INT, "print", params, null, p),
                                      new ParameterListNode(callArgs),
                                      p);
        ifStatement.setNext(procCall);
        final BaseBlock baseBlock = BaseBlock.getInstance();

        ifStatement.transformToSSA(baseBlock, null);

        // validate condition
        final Instruction firstInstruction = baseBlock.getFirstInstruction();
        assertIsBinOpInstructionOfConstants(firstInstruction,
                                            EQUAL,
                                            const1.getValue(),
                                            const1.getValue());

        // check the 'then' branch
        final BaseBlock thenBlock = baseBlock.getBranch();
        assertNotNull(thenBlock);
        final Instruction secondInstruction = thenBlock.getFirstInstruction();
        assertIsBinOpInstructionOfConstants(secondInstruction,
                                            PLUS,
                                            const1.getValue(),
                                            const2.getValue());
        // check the 'else' branch
        final BaseBlock failBlock = baseBlock.getFail();
        assertNotNull(failBlock);
        final Instruction thirdInstruction = failBlock.getFirstInstruction();
        assertIsBinOpInstructionOfConstants(thirdInstruction,
                                            MINUS,
                                            const1.getValue(),
                                            const2.getValue());
        // check the join block
        final BaseBlock joinBlock = thenBlock.getBranch();
        assertTrue(joinBlock instanceof JoinBlock);
        assertSame(thenBlock.getBranch(), failBlock.getBranch());
        assertTrue(joinBlock.getFirstInstruction() instanceof SpecialInstruction);
        final SpecialInstruction phiInstr = (SpecialInstruction) joinBlock.getFirstInstruction();
        assertEquals(SpecialInstruction.Type.PHI, phiInstr.getType());
        final Operand[] phiOperands = phiInstr.getOperands();
        assertEquals(2, phiOperands.length);
        assertTrue(phiOperands[0] instanceof InstructionOperand);
        assertTrue(phiOperands[1] instanceof InstructionOperand);
        assertEquals(secondInstruction, ((InstructionOperand) phiOperands[0]).getInstruction());
        assertEquals(thirdInstruction, ((InstructionOperand) phiOperands[1]).getInstruction());

        assertNotNull(joinBlock.getBranch());
        final BaseBlock lastBlock = joinBlock.getBranch();
        assertTrue(lastBlock.getFirstInstruction() instanceof SpecialInstruction);
        final SpecialInstruction fifthInstruction =
                (SpecialInstruction) lastBlock.getFirstInstruction();
        assertEquals(SpecialInstruction.Type.DISPATCH, fifthInstruction.getType());
        final Operand[] dispatchOps = fifthInstruction.getOperands();
        assertEquals(4, dispatchOps.length);
        assertTrue(dispatchOps[3] instanceof InstructionOperand);
        assertEquals(phiInstr, ((InstructionOperand) dispatchOps[3]).getInstruction());
    }

    @Test
    void assignmentsInWhileLoop() {
        /*
         * code:
         * while(a<=10){a=a*2;}
         * return a;
         * expected result:
         * 0: phi a, (2)    [in baseBlock]
         * 1: leq (0), 10   [in baseBlock]
         * 2: mul (0), 2    [in loopBlock]
         * 3: return (0)    [in lastBlock]
         */
        final ConstantNode<Long> const2 = new ConstantNode<>(INT, 2L, p);
        final ConstantNode<Long> const10 = new ConstantNode<>(INT, 10L, p);
        final VariableDescription aDescr = new VariableDescription("a", INT, false);
        final VariableAccessNode a = new VariableAccessNode(aDescr, p);
        final BinaryOperationNode ax2 = new BinaryOperationNode(a, TIMES, const2);
        final BinaryOperationNode condition = new BinaryOperationNode(a, LOWER_EQUAL, const10);
        final AssignmentNode assignment = new AssignmentNode(a, ax2);
        final WhileNode whileNode = new WhileNode(condition, assignment, p);
        whileNode.setNext(new ReturnNode(a, p));

        final BaseBlock baseBlock = BaseBlock.getInstance();
        whileNode.transformToSSA(baseBlock, null);

        // assert first instruction is a phi instruction
        assertTrue(baseBlock.getFirstInstruction() instanceof SpecialInstruction);
        final SpecialInstruction phiInstr = (SpecialInstruction) baseBlock.getFirstInstruction();
        assertEquals(SpecialInstruction.Type.PHI, phiInstr.getType());
        final Operand[] phiOps = phiInstr.getOperands();
        assertTrue(phiOps[0] instanceof ParameterOperand);
        assertEquals(aDescr, ((ParameterOperand) phiOps[0]).getVariable());
        assertTrue(phiOps[1] instanceof InstructionOperand);

        // assert second instruction is a binary operation instruction between phi and constant
        assertTrue(phiInstr.getNext() instanceof BinaryOperationInstruction);
        final BinaryOperationInstruction condInstr =
                (BinaryOperationInstruction) phiInstr.getNext();
        assertTrue(condInstr.getOperands()[0] instanceof InstructionOperand);
        assertEquals(phiInstr, ((InstructionOperand) condInstr.getOperands()[0]).getInstruction());
        assertTrue(condInstr.getOperands()[1] instanceof Constant);
        assertEquals(const10.getValue(), ((Constant) condInstr.getOperands()[1]).getValue());

        // assert baseBlock has the loop body as its branch
        final BaseBlock loopBody = baseBlock.getBranch();
        assertNotNull(loopBody);
        assertEquals(loopBody.getBranch(), baseBlock); // assert loop points back to condition
        assertTrue(loopBody.getFirstInstruction() instanceof BinaryOperationInstruction);
        final BinaryOperationInstruction mulInstr =
                (BinaryOperationInstruction) loopBody.getFirstInstruction();
        assertTrue(mulInstr.getOperands()[0] instanceof InstructionOperand);
        assertEquals(phiInstr, ((InstructionOperand) mulInstr.getOperands()[0]).getInstruction());
        assertTrue(mulInstr.getOperands()[1] instanceof Constant);
        assertEquals(const2.getValue(), ((Constant) mulInstr.getOperands()[1]).getValue());

        // assert baseBlock has the part after the loop as its fail
        final BaseBlock followUp = baseBlock.getFail();
        assertNotNull(followUp);
        assertTrue(followUp.getFirstInstruction() instanceof SpecialInstruction);
        final SpecialInstruction retInstr = (SpecialInstruction) followUp.getFirstInstruction();
        assertEquals(SpecialInstruction.Type.RETURN, retInstr.getType());
        assertTrue(retInstr.getOperands()[0] instanceof InstructionOperand);
        assertEquals(phiInstr, ((InstructionOperand) retInstr.getOperands()[0]).getInstruction());
    }

    /**
     * Encapsulates all assertions for BinaryOperationInstructions that operate on Constants.
     *
     * @param instruction the instruction that is tested
     * @param type the type the instruction should have
     * @param firstValue the expected value of the left operand
     * @param secondValue the expected value of the right operand
     */
    private static void assertIsBinOpInstructionOfConstants(@Nullable final Instruction instruction,
                                                            @NotNull final BinaryOperationNodeType type,
                                                            final long firstValue,
                                                            final long secondValue) {

        assertTrue(instruction instanceof BinaryOperationInstruction);
        assertEquals(type, ((BinaryOperationInstruction) instruction).getType());
        final Operand[] firstInstrOps = instruction.getOperands();
        assertEquals(2, firstInstrOps.length);
        assertTrue(firstInstrOps[0] instanceof Constant);
        assertTrue(firstInstrOps[1] instanceof Constant);
        assertEquals(firstValue, ((Constant) firstInstrOps[0]).getValue());
        assertEquals(secondValue, ((Constant) firstInstrOps[1]).getValue());
    }
}
