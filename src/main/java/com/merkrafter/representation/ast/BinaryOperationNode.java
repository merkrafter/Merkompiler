package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

import java.util.List;

/****
 * This AST node represents a binary operation. It therefore has two child nodes.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class BinaryOperationNode extends ASTBaseNode {
    // ATTRIBUTES
    //==============================================================
    private final AbstractSyntaxTree leftOperand;
    private final AbstractSyntaxTree rightOperand;

    private final BinaryOperationNodeType binOpType;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new BinaryOperationNode from an operation and two operands.
     * It does no type validation at this point.
     ***************************************************************/
    public BinaryOperationNode(final AbstractSyntaxTree leftOperand,
                               final BinaryOperationNodeType binOpType,
                               final AbstractSyntaxTree rightOperand) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.binOpType = binOpType;
    }

    // GETTER
    //==============================================================
    public BinaryOperationNodeType getBinOpType() {
        return binOpType;
    }

    /**
     * After evaluating this node, this is the type that is propagated upwards.
     *
     * @return the return type of this node
     */
    @Override
    public Type getReturnedType() {
        // TODO regularly check this method
        /* since javac can not see that a switch statement covers all enum items the return BOOLEAN
           at the bottom in necessary */
        switch (binOpType) {

            case PLUS:
            case MINUS:
            case TIMES:
            case DIVIDE:
                return Type.INT;
            /*
            break;
            case LOWER:
            case LOWER_EQUAL:
            case EQUAL:
            case GREATER_EQUAL:
            case GREATER:
                return Type.BOOLEAN;
             */
        }
        return Type.BOOLEAN;
    }

    /**
     * A BinaryOperationNode has a semantics error if any child node is null or has an error itself.
     *
     * @return whether the tree represented by this node has a semantics error somewhere
     */
    @Override
    public boolean hasSemanticsError() {
        return leftOperand == null || rightOperand == null || binOpType == null
               || leftOperand.hasSemanticsError() || rightOperand.hasSemanticsError();
    }

    /**
     * A BinaryOperationNode has a syntax error if any child node is null or has an error itself.
     *
     * @return whether the tree represented by this node has a syntax error somewhere
     */
    @Override
    public boolean hasSyntaxError() {
        return leftOperand == null || rightOperand == null || binOpType == null
               || leftOperand.hasSyntaxError() || rightOperand.hasSyntaxError();
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(leftOperand, rightOperand);
    }
}
