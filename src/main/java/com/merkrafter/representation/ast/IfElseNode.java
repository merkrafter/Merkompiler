package com.merkrafter.representation.ast;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.ssa.BaseBlock;
import com.merkrafter.representation.ssa.JoinBlock;
import com.merkrafter.representation.ssa.SSATransformableStatement;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/****
 * This AST node represents an if-else construct. The if branch is handled by
 * a separate IfNode instance and the else branch is simply implemented as a sequence of Statements.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class IfElseNode extends AbstractStatementNode implements SSATransformableStatement {
    // ATTRIBUTES
    //==============================================================
    @NotNull
    private final IfNode ifBranch;
    @NotNull
    private final Statement elseBranch;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new IfElseNode from a node handling the if branch and a node that is executed
     * if the condition does not hold.
     * The constructor does not perform a type check.
     ***************************************************************/
    public IfElseNode(@NotNull final IfNode ifBranch, @NotNull final Statement elseBranch) {
        this.ifBranch = ifBranch;
        this.elseBranch = elseBranch;
    }

    // GETTER
    //==============================================================

    @NotNull
    @Override
    public Position getPosition() {
        return ifBranch.getPosition();
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @NotNull
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(ifBranch, elseBranch, getNext());
    }

    /**
     * Two IfElseNodes are considered equal if their ifNodes and children are non-null and are
     * equal to each other respectively.
     */
    @Override
    public boolean equals(@NotNull final Object obj) {
        if (!(obj instanceof IfElseNode)) {
            return false;
        }
        final IfElseNode other = (IfElseNode) obj;
        return elseBranch.equals(other.elseBranch) && ifBranch.equals(other.ifBranch);
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
        dotRepr.append(ifBranch.getDotRepresentation());
        dotRepr.append(System.lineSeparator());
        dotRepr.append(elseBranch.getDotRepresentation());
        dotRepr.append(System.lineSeparator());

        // define this
        dotRepr.append(String.format("%d[label=%s];", getID(), "IF_ELSE"));
        dotRepr.append(System.lineSeparator());

        // define links
        if (getNext() != null) {
            dotRepr.append(String.format("%d -> %d;", getID(), getNext().getID()));
            dotRepr.append(System.lineSeparator());
        }
        dotRepr.append(String.format("%d -> %d;", getID(), ifBranch.hashCode()));
        dotRepr.append(System.lineSeparator());
        dotRepr.append(String.format("%d -> %d;", getID(), elseBranch.getID()));
        dotRepr.append(System.lineSeparator());

        return dotRepr.toString();
    }

    /**
     * @return whether there is a return statement in this statement sequence
     */
    @Override
    public boolean hasReturnStatement() {
        return super.hasReturnStatement()
               || ifBranch.hasReturnStatement() && elseBranch.hasReturnStatement();
    }

    /**
     * Returns whether this given return type is compatible with the next statements in this
     * sequence or with both branches of this IfElseNode.
     */
    @Override
    public boolean isCompatibleToType(@NotNull Type type) {
        final boolean afterCorrect = super.isCompatibleToType(type);
        final boolean afterNoConflict = afterCorrect || !super.hasReturnStatement();
        final boolean ifBranchCorrect = ifBranch.hasReturnType(type);
        final boolean elseBranchCorrect = elseBranch.isCompatibleToType(type);
        final boolean ifBranchNoConflict = ifBranchCorrect || !ifBranch.hasReturnStatement();
        final boolean elseBranchNoConflict = elseBranchCorrect || !elseBranch.hasReturnStatement();

        return afterCorrect && ifBranchNoConflict && elseBranchNoConflict
               || ifBranchCorrect && elseBranchCorrect && afterNoConflict;
    }

    @NotNull
    @Override
    public List<String> getTypingErrors() {
        final List<String> errors = new LinkedList<>();
        errors.addAll(super.getTypingErrors());
        errors.addAll(ifBranch.getTypingErrors());
        errors.addAll(elseBranch.getTypingErrors());
        return errors;
    }

    /**
     * @param baseBlock the block that all instructions are inserted into
     * @param outerJoinBlock if this IfNode is part of nested ifs/whiles: JoinBlock from outer scope
     */
    @Override
    public void transformToSSA(@NotNull final BaseBlock baseBlock, final JoinBlock outerJoinBlock) {
        final JoinBlock joinBlock = new JoinBlock();
        final BaseBlock thenBlock = BaseBlock.getInstance();
        joinBlock.setEnvironment(JoinBlock.Environment.IFELSE);
        thenBlock.setBranch(joinBlock);
        baseBlock.setBranch(thenBlock);

        ifBranch.transformToSSA(baseBlock, outerJoinBlock);
        if (elseBranch instanceof SSATransformableStatement) {
            final BaseBlock failBlock = BaseBlock.getInstance();
            failBlock.setBranch(joinBlock);
            baseBlock.setFail(failBlock);
            joinBlock.setUpdatePosition(JoinBlock.Position.SECOND);
            ((SSATransformableStatement) elseBranch).transformToSSA(failBlock, joinBlock);
            joinBlock.commitPhi();
        }

        final BaseBlock nextBaseBlock = BaseBlock.getInstance();
        joinBlock.setBranch(nextBaseBlock);
        if (getNext() instanceof SSATransformableStatement) {
            ((SSATransformableStatement) getNext()).transformToSSA(nextBaseBlock, outerJoinBlock);
        }
    }
}
