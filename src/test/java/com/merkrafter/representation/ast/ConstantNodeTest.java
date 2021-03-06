package com.merkrafter.representation.ast;

import static org.junit.jupiter.api.Assertions.*;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.VariableDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ConstantNodeTest {

  private final Position p = new Position("", 0, 0); // just a dummy position

  /** A ConstantNode should return the correct type. In this case, it's an integer. */
  @Test
  void getReturnedType() {
    final ConstantNode<Integer> node = new ConstantNode<>(Type.INT, 5, p);
    assertEquals(Type.INT, node.getReturnedType());
  }

  /** A ConstantNode should return the correct value. */
  @ParameterizedTest
  @ValueSource(ints = {Integer.MAX_VALUE, Integer.MIN_VALUE, -1, 0, 1, 5})
  void getValue(final int value) {
    final ConstantNode<Integer> node = new ConstantNode<>(Type.INT, value, p);
    assertEquals(value, node.getValue());
  }

  /** Two ConstantNodes that were created identically should be equal. */
  @ParameterizedTest
  @ValueSource(ints = {Integer.MAX_VALUE, Integer.MIN_VALUE, -1, 0, 1, 5})
  void testEquals(final int value) {
    final ConstantNode<Integer> node1 = new ConstantNode<>(Type.INT, value, p);
    final ConstantNode<Integer> node2 = new ConstantNode<>(Type.INT, value, p);
    assertEquals(node1, node2);
  }

  /**
   * A ConstantNode should not be equal to a VariableAccesNode even if the latter stores a constant
   * variable with the same value.
   */
  @Test
  void testEquals() {
    final int value = 5;
    final ConstantNode<Integer> constantNode = new ConstantNode<>(Type.INT, value, p);
    final VariableAccessNode variableNode =
        new VariableAccessNode(new VariableDescription("", Type.INT, value, true), p);
    assertNotEquals(constantNode, variableNode);
  }
}
