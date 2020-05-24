package com.merkrafter.representation.ast;

import static org.junit.jupiter.api.Assertions.*;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.Type;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class BinaryOperationNodeTest {

  private final Position p = new Position("", 0, 0); // just a dummy position

  /** Adding, subtracting, multiplying and dividing two integers should return an integer. */
  @ParameterizedTest
  @EnumSource(
      value = BinaryOperationNodeType.class,
      names = {"PLUS", "MINUS", "TIMES", "DIVIDE"})
  void integerOperation(@NotNull final BinaryOperationNodeType integerOp) {
    final ConstantNode<Integer> leftOperand = new ConstantNode<>(Type.INT, 10, p);
    final ConstantNode<Integer> rightOperand = new ConstantNode<>(Type.INT, 5, p);
    final BinaryOperationNode node = new BinaryOperationNode(leftOperand, integerOp, rightOperand);
    assertEquals(Type.INT, node.getReturnedType());
  }

  /** Comparing two integers should return a boolean value. */
  @ParameterizedTest
  @EnumSource(
      value = BinaryOperationNodeType.class,
      names = {"LOWER", "LOWER_EQUAL", "EQUAL", "GREATER_EQUAL", "GREATER"})
  void comparisonOperation(@NotNull final BinaryOperationNodeType comparisonOp) {
    final ConstantNode<Integer> leftOperand = new ConstantNode<>(Type.INT, 10, p);
    final ConstantNode<Integer> rightOperand = new ConstantNode<>(Type.INT, 5, p);
    final BinaryOperationNode node =
        new BinaryOperationNode(leftOperand, comparisonOp, rightOperand);
    assertEquals(Type.BOOLEAN, node.getReturnedType());
  }

  /** Comparing an integer to the result of an integer addition should return a boolean value. */
  @ParameterizedTest
  @EnumSource(
      value = BinaryOperationNodeType.class,
      names = {"LOWER", "LOWER_EQUAL", "EQUAL", "GREATER_EQUAL", "GREATER"})
  void comparisonOperationAfterIntegerAddition(
      @NotNull final BinaryOperationNodeType comparisonOp) {
    // those 3 nodes represent "10 + 5"
    final ConstantNode<Integer> leftSummand = new ConstantNode<>(Type.INT, 10, p);
    final ConstantNode<Integer> rightSummand = new ConstantNode<>(Type.INT, 5, p);
    final BinaryOperationNode additionNode =
        new BinaryOperationNode(leftSummand, BinaryOperationNodeType.PLUS, rightSummand);

    // those nodes represent "(10+5) compare 15"
    final ConstantNode<Integer> compareValue = new ConstantNode<>(Type.INT, 15, p);
    final BinaryOperationNode comparisonNode =
        new BinaryOperationNode(additionNode, comparisonOp, compareValue);
    assertEquals(Type.BOOLEAN, comparisonNode.getReturnedType());
  }
}
