package com.merkrafter.representation.ast;

import com.merkrafter.representation.ActualProcedureDescription;
import com.merkrafter.representation.ProcedureDescription;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.VariableDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    @NotNull
    private final ProcedureDescription voidFunc =
            new ActualProcedureDescription(Type.VOID, "voidFunc", new LinkedList<>(), null);
    @NotNull
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

    /**
     * void voidFunc() {...}
     * void singleArg(int arg) {...}
     * singleArg(voidFunc()); // should return an error
     */
    @Test
    void testVoidAsArgument() {
        // define a procedure that takes a single int-type argument
        final LinkedList<VariableDescription> args = new LinkedList<>();
        args.add(new VariableDescription("arg", Type.INT, 0, false));
        final ProcedureDescription singleArgumentProcedure =
                new ActualProcedureDescription(Type.VOID, "singleArg", args, null);

        // describe a call of that procedure with a void-returning procedure as an argument
        final LinkedList<Expression> params = new LinkedList<>();
        params.add(voidFuncCall);
        final ParameterListNode paramNode = new ParameterListNode(params);
        final ProcedureCallNode nodeUnderTest =
                new ProcedureCallNode(singleArgumentProcedure, paramNode);

        final List<String> errors = nodeUnderTest.getTypingErrors();
        assertFalse(errors.isEmpty());
    }
}
