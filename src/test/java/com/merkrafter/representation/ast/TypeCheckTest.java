package com.merkrafter.representation.ast;

import com.merkrafter.representation.ActualProcedureDescription;
import com.merkrafter.representation.ProcedureDescription;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.VariableDescription;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

/****
 * Test cases in this class verify the type checking system works
 * as intended.
 *
 * @since v0.4.0
 * @author merkrafter
 ***************************************************************/
public class TypeCheckTest {

    private final VariableDescription var1 = new VariableDescription("var1", Type.INT, 0, false);
    private final VariableDescription var2 = new VariableDescription("var2", Type.INT, 0, false);
    private final ProcedureDescription voidFunc =
            new ActualProcedureDescription(Type.VOID, "voidFunc", new LinkedList<>(), null);
    private final ProcedureCallNode voidFuncCall =
            new ProcedureCallNode(voidFunc, new ParameterListNode(new LinkedList<>()));

    /**
     * An assignment of the "return value" of a void function to an variable should not be allowed
     */
    @Test
    void testVoidInAssignment() {
        // var1 = voidFunc();
        final AssignmentNode nodeUnderTest =
                new AssignmentNode(new VariableAccessNode(var1), voidFuncCall);
        final List<String> errors = nodeUnderTest.getTypingErrors();
        assertFalse(errors.isEmpty());
    }

    /**
     * An arithmetic expression of an integer variable with a void function should not be allowed.
     */
    @Test
    void testVoidInExpression() {
        // var1 = voidFunc() * var2;
        final Expression expressionUnderTest = new BinaryOperationNode(voidFuncCall,
                                                                       BinaryOperationNodeType.TIMES,
                                                                       new VariableAccessNode(var2));
        final List<String> errors = expressionUnderTest.getTypingErrors();
        assertFalse(errors.isEmpty());
    }

    @Test
    void testVoidInCondition() {
        /*
         * while(voidFunc()){...}
         */
        final WhileNode nodeUnderTest = new WhileNode(voidFuncCall,
                                                      // random statement
                                                      new ReturnNode());
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
        final AssignmentNode nodeUnderTest = new AssignmentNode(new VariableAccessNode(var1),
                                                                new BinaryOperationNode(voidFuncCall,
                                                                                        BinaryOperationNodeType.PLUS,
                                                                                        voidFuncCall));
        final List<String> errors = nodeUnderTest.getTypingErrors();
        assertFalse(errors.isEmpty());
    }

    @Test
    void testVoidComparison() {
        // while(voidFunc() == voidFunc()) {...}
        final WhileNode nodeUnderTest = new WhileNode(new BinaryOperationNode(voidFuncCall,
                                                                              BinaryOperationNodeType.EQUAL,
                                                                              voidFuncCall),
                                                      // random statement
                                                      new ReturnNode());
        final List<String> errors = nodeUnderTest.getTypingErrors();
        assertFalse(errors.isEmpty());
    }

    @Test
    void testVoidReturn() {
        // return voidFunc();
        final ReturnNode nodeUnderTest = new ReturnNode(voidFuncCall);
        final List<String> errors = nodeUnderTest.getTypingErrors();
        assertFalse(errors.isEmpty());
    }
}
