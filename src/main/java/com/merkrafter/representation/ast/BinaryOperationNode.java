package com.merkrafter.representation.ast;

import com.merkrafter.representation.Type;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import static com.merkrafter.representation.ast.AbstractStatementNode.collectErrorsFrom;

/****
 * This AST node represents a binary operation. It therefore has two child nodes.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class BinaryOperationNode implements Expression {
    // ATTRIBUTES
    //==============================================================
    @NotNull
    private final Expression leftOperand;
    @NotNull
    private final Expression rightOperand;

    @NotNull
    private final BinaryOperationNodeType binOpType;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new BinaryOperationNode from an operation and two operands.
     * It does no type validation at this point.
     ***************************************************************/
    public BinaryOperationNode(@NotNull final Expression leftOperand,
                               @NotNull final BinaryOperationNodeType binOpType,
                               @NotNull final Expression rightOperand) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.binOpType = binOpType;
    }

    // GETTER
    //==============================================================

    /**
     * After evaluating this node, this is the type that is propagated upwards.
     *
     * @return the return type of this node
     */
    @NotNull
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

    @NotNull
    @Override
    public List<String> getTypingErrors() {
        final List<String> errors = new LinkedList<>();
        errors.addAll(leftOperand.getTypingErrors());
        errors.addAll(rightOperand.getTypingErrors());
        if (!leftOperand.getReturnedType().equals(rightOperand.getReturnedType())) {
            errors.add(String.format("Type mismatch in expression: %s and %s",
                                     leftOperand.getReturnedType(),
                                     rightOperand.getReturnedType()));
        }
        if (leftOperand.getReturnedType().equals(Type.VOID) || rightOperand.getReturnedType()
                                                                           .equals(Type.VOID)) {
            errors.add("Type mismatch in expression: void must not occur in expression");
        }
        return errors;
    }

    /**
     * A BinaryOperationNode has a semantics error if any child node is null or has an error itself.
     *
     * @return whether the tree represented by this node has a semantics error somewhere
     */
    @Override
    public boolean hasSemanticsError() {
        return leftOperand.hasSemanticsError() || rightOperand.hasSemanticsError();
    }

    /**
     * A BinaryOperationNode has a syntax error if any child node is null or has an error itself.
     *
     * @return whether the tree represented by this node has a syntax error somewhere
     */
    @Override
    public boolean hasSyntaxError() {
        return leftOperand.hasSyntaxError() || rightOperand.hasSyntaxError();
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @NotNull
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(leftOperand, rightOperand);
    }

    /**
     * Two BinaryOperationNodes are considered equal if their expressions and op types are non-null
     * and are equal to each other respectively.
     */
    @Override
    public boolean equals(@NotNull final Object obj) {
        if (!(obj instanceof BinaryOperationNode)) {
            return false;
        }
        final BinaryOperationNode other = (BinaryOperationNode) obj;
        return binOpType == other.binOpType && leftOperand.equals(other.leftOperand) && rightOperand
                .equals(other.rightOperand);
    }

    /**
     * @return an identifier unique in the whole AST
     */
    @Override
    public int getID() {
        return hashCode();
    }

    /**
     * @return dot/graphviz declarations of this component's children
     */
    @NotNull
    @Override
    public String getDotRepresentation() {
        final StringBuilder dotRepr = new StringBuilder();

        // define children
        dotRepr.append(leftOperand.getDotRepresentation());
        dotRepr.append(System.lineSeparator());
        dotRepr.append(rightOperand.getDotRepresentation());
        dotRepr.append(System.lineSeparator());

        // define this
        dotRepr.append(String.format("%d[label=\"BINOP%s%s\"];",
                                     getID(),
                                     System.lineSeparator(),
                                     binOpType.name()));
        dotRepr.append(System.lineSeparator());

        // define links
        dotRepr.append(String.format("%d -> %d;", getID(), leftOperand.getID()));
        dotRepr.append(System.lineSeparator());
        dotRepr.append(String.format("%d -> %d;", getID(), rightOperand.getID()));
        dotRepr.append(System.lineSeparator());

        return dotRepr.toString();
    }
}
