package com.merkrafter.representation.ast;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.ActualProcedureDescription;
import com.merkrafter.representation.ProcedureDescription;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.VariableDescription;
import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

/****
 * Test cases in this class verify the type checking system works
 * as intended.
 *
 * @since v0.4.0
 * @author merkrafter
 ***************************************************************/
public class TypeCheckTest {

  private final Position p = new Position("", 0, 0);

  private final VariableDescription var1 = new VariableDescription("var1", Type.INT, 0, false);
  private final VariableDescription var2 = new VariableDescription("var2", Type.INT, 0, false);

  @NotNull
  private final ProcedureDescription voidFunc =
      new ActualProcedureDescription(Type.VOID, "voidFunc", new LinkedList<>(), null, p);

  @NotNull
  private final ProcedureCallNode voidFuncCall =
      new ProcedureCallNode(voidFunc, new ParameterListNode(new LinkedList<>()), p);

  /** An assignment of the "return value" of a void function to an variable should not be allowed */
  @Test
  void testVoidInAssignment() {
    // var1 = voidFunc();
    final AssignmentNode nodeUnderTest =
        new AssignmentNode(new VariableAccessNode(var1, p), voidFuncCall);
    final List<String> errors = nodeUnderTest.getTypingErrors();
    assertFalse(errors.isEmpty());
  }

  /** An arithmetic expression of an integer variable with a void function should not be allowed. */
  @Test
  void testVoidInExpression() {
    // var1 = voidFunc() * var2;
    final Expression expressionUnderTest =
        new BinaryOperationNode(
            voidFuncCall, BinaryOperationNodeType.TIMES, new VariableAccessNode(var2, p));
    final List<String> errors = expressionUnderTest.getTypingErrors();
    assertFalse(errors.isEmpty());
  }

  @Test
  void testVoidInCondition() {
    /*
     * while(voidFunc()){...}
     */
    final WhileNode nodeUnderTest =
        new WhileNode(
            voidFuncCall,
            // random statement
            new ReturnNode(p),
            p);
    final List<String> errors = nodeUnderTest.getTypingErrors();
    assertFalse(errors.isEmpty());
  }

  @Test
  void testVoidAddition() {
    // voidFunc() + voidFunc()
    final Expression expressionUnderTest =
        new BinaryOperationNode(voidFuncCall, BinaryOperationNodeType.PLUS, voidFuncCall);
    final List<String> errors = expressionUnderTest.getTypingErrors();
    assertFalse(errors.isEmpty());
  }

  @Test
  void testVoidAdditionAssignment() {
    // var1 = voidFunc() + voidFunc()
    final AssignmentNode nodeUnderTest =
        new AssignmentNode(
            new VariableAccessNode(var1, p),
            new BinaryOperationNode(voidFuncCall, BinaryOperationNodeType.PLUS, voidFuncCall));
    final List<String> errors = nodeUnderTest.getTypingErrors();
    assertFalse(errors.isEmpty());
  }

  @Test
  void testVoidComparison() {
    // while(voidFunc() == voidFunc()) {...}
    final WhileNode nodeUnderTest =
        new WhileNode(
            new BinaryOperationNode(voidFuncCall, BinaryOperationNodeType.EQUAL, voidFuncCall),
            // random statement
            new ReturnNode(p),
            p);
    final List<String> errors = nodeUnderTest.getTypingErrors();
    assertFalse(errors.isEmpty());
  }

  @Test
  void testVoidReturn() {
    // return voidFunc();
    final ReturnNode nodeUnderTest = new ReturnNode(voidFuncCall, p);
    final List<String> errors = nodeUnderTest.getTypingErrors();
    assertFalse(errors.isEmpty());
  }

  /**
   * void voidFunc() {...} void singleArg(int arg) {...} singleArg(voidFunc()); // should return an
   * error
   */
  @Test
  void testVoidAsArgument() {
    // define a procedure that takes a single int-type argument
    final LinkedList<VariableDescription> args = new LinkedList<>();
    args.add(new VariableDescription("arg", Type.INT, 0, false));
    final ProcedureDescription singleArgumentProcedure =
        new ActualProcedureDescription(Type.VOID, "singleArg", args, null, p);

    // describe a call of that procedure with a void-returning procedure as an argument
    final LinkedList<Expression> params = new LinkedList<>();
    params.add(voidFuncCall);
    final ParameterListNode paramNode = new ParameterListNode(params);
    final ProcedureCallNode nodeUnderTest =
        new ProcedureCallNode(singleArgumentProcedure, paramNode, p);

    final List<String> errors = nodeUnderTest.getTypingErrors();
    assertFalse(errors.isEmpty());
  }
}
