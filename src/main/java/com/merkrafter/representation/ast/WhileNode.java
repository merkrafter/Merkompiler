package com.merkrafter.representation.ast;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.ssa.BaseBlock;
import com.merkrafter.representation.ssa.JoinBlock;
import com.merkrafter.representation.ssa.SSATransformableExpression;
import com.merkrafter.representation.ssa.SSATransformableStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/****
 * This AST node represents a while construct. It is very similar to the IfNode but can not
 * be interchanged with it as there is not while-else construct, for instance.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class WhileNode extends AbstractStatementNode implements SSATransformableStatement {
    // ATTRIBUTES
    //==============================================================
    @NotNull
    private final Expression condition;
    @NotNull
    private final Statement loopBody;
    @NotNull
    private final Position position;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new WhileNode from a node representing a condition and a node that is executed
     * while the condition holds.
     * The constructor does not perform a type check.
     ***************************************************************/
    public WhileNode(@NotNull final Expression condition, @NotNull final Statement loopBody,
                     @NotNull final Position position) {
        this.condition = condition;
        this.loopBody = loopBody;
        this.position = position;
    }

    // GETTER
    //==============================================================

    @NotNull
    @Override
    public Position getPosition() {
        return position;
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @NotNull
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(condition, loopBody, getNext());
    }

    /**
     * Two WhileNodes are considered equal if their conditions and children are non-null and are
     * equal to each other respectively.
     */
    @Override
    public boolean equals(@NotNull final Object obj) {
        if (!(obj instanceof WhileNode)) {
            return false;
        }
        final WhileNode other = (WhileNode) obj;
        return condition.equals(other.condition) && loopBody.equals(other.loopBody);
    }

    /**
     * @return dot/graphviz declarations of this component's children
     */
    @NotNull
    @Override
    public String getDotRepresentation() {
        final StringBuilder dotRepr = new StringBuilder(super.getDotRepresentation());
        dotRepr.append(System.lineSeparator());

        // define children
        dotRepr.append(condition.getDotRepresentation());
        dotRepr.append(System.lineSeparator());
        dotRepr.append(loopBody.getDotRepresentation());
        dotRepr.append(System.lineSeparator());

        // define this
        dotRepr.append(String.format("%d[label=%s];", getID(), "WHILE"));
        dotRepr.append(System.lineSeparator());

        // define links
        dotRepr.append(String.format("%d -> %d;", getID(), condition.getID()));
        dotRepr.append(System.lineSeparator());
        if (getNext() != null) {
            dotRepr.append(String.format("%d -> %d;", getID(), getNext().getID()));
            dotRepr.append(System.lineSeparator());
        }
        dotRepr.append(String.format("%d -> %d;", getID(), loopBody.getID()));
        dotRepr.append(System.lineSeparator());

        // return
        return dotRepr.toString();
    }

    @NotNull
    @Override
    public List<String> getTypingErrors() {
        final List<String> errors = super.getTypingErrors();
        errors.addAll(condition.getTypingErrors());
        if (!condition.getReturnedType().equals(Type.BOOLEAN)) {
            errors.add(String.format("%s: Condition does not evaluate to boolean in if statement",
                                     condition.getPosition()));
        }
        errors.addAll(loopBody.getTypingErrors());
        return errors;
    }

    /**
     * @return whether statements inside AND after the loop comply
     */
    @Override
    public boolean isCompatibleToType(final @NotNull Type type) {
        /*
         * As it is not certain that the loop body will be executed, the next statements after the
         * loop must comply either way.
         * If the loop body does not comply, this could mean it lacks a return statement (which
         * would be okay) or has a return statement with an incompatible type, which must be caught.
         */
        return super.isCompatibleToType(type) && (loopBody.isCompatibleToType(type)
                                                  || !loopBody.hasReturnStatement());
    }

    @Override
    public void transformToSSA(final @NotNull BaseBlock baseBlock,
                               final @Nullable JoinBlock outerJoinBlock) {
        final JoinBlock joinBlock = new JoinBlock(baseBlock);
        joinBlock.setEnvironment(JoinBlock.Environment.WHILE);
        joinBlock.setUpdatePosition(JoinBlock.Position.SECOND);
        if (condition instanceof SSATransformableExpression) {
            ((SSATransformableExpression) condition).transformToSSA(joinBlock);
        }
        if (loopBody instanceof SSATransformableStatement) {
            final BaseBlock loopBodyBlock = BaseBlock.getInstance();
            joinBlock.setBranch(loopBodyBlock);
            loopBodyBlock.setBranch(baseBlock);
            ((SSATransformableStatement) loopBody).transformToSSA(loopBodyBlock, joinBlock);
            joinBlock.commitPhi(outerJoinBlock);
            joinBlock.renamePhi(joinBlock);
            joinBlock.renamePhi(loopBodyBlock, joinBlock, false);
        }
        if (getNext() instanceof SSATransformableStatement) {
            final BaseBlock failBlock = BaseBlock.getInstance();
            baseBlock.setFail(failBlock);
            ((SSATransformableStatement) getNext()).transformToSSA(failBlock, outerJoinBlock);
        } else if (outerJoinBlock != null) {
            baseBlock.setFail(outerJoinBlock);
        }
    }
}
