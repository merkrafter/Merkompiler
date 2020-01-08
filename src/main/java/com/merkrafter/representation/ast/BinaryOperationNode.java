package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;

/****
 * This AST node represents a binary operation. It therefore has two child nodes.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class BinaryOperationNode extends ASTBaseNode {
    // ATTRIBUTES
    //==============================================================
    private final ASTBaseNode leftOperand;
    private final ASTBaseNode rightOperand;

    private final BinaryOperationNodeType binOpType;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new BinaryOperationNode from an operation and two operands.
     * It does no type validation at this point.
     ***************************************************************/
    public BinaryOperationNode(final ASTBaseNode leftOperand,
                               final BinaryOperationNodeType binOpType,
                               final ASTBaseNode rightOperand) {
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
    Type getReturnedType() {
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
}
