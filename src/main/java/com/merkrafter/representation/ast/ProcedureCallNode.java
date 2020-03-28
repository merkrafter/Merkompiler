package com.merkrafter.representation.ast;

import com.merkrafter.lexing.Position;
import com.merkrafter.representation.ProcedureDescription;
import com.merkrafter.representation.Type;
import com.merkrafter.representation.ssa.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

/****
 * This AST node represents the call to a procedure or method in a JavaSST program.
 *
 * @since v0.3.0
 * @author merkrafter
 ***************************************************************/
public class ProcedureCallNode extends AbstractStatementNode implements Expression, SSATransformableExpression, SSATransformableStatement {
    // ATTRIBUTES
    //==============================================================
    @NotNull
    private final ProcedureDescription procedure;
    @NotNull
    private final ParameterListNode args;
    @NotNull
    private final Position position;
    @Nullable
    private Operand operand;

    // CONSTRUCTORS
    //==============================================================

    /****
     * Creates a new node that represents a procedure call with the
     * given arguments.
     ***************************************************************/
    public ProcedureCallNode(@NotNull final ProcedureDescription procedure,
                             @NotNull final ParameterListNode args,
                             @NotNull final Position position) {
        this.procedure = procedure;
        this.args = args;
        this.position = position;
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
        final Type returnedType = procedure.getReturnType();
        // this can only happen if procedure is an unresolved proxy which should be handled before
        assert returnedType != null;
        return returnedType;
    }

    @NotNull
    @Override
    public List<String> getTypingErrors() {
        final List<String> errors = new LinkedList<>();
        if (procedure.getParamList() == null) {
            errors.add(String.format(
                    "%s: Could not verify the arguments in call to unknown procedure %s",
                    getPosition(),
                    procedure.getName()));
        }
        // this case should never happen because the procedure call should only be created when
        // the types of call arguments and formal parameters match, but better check this twice
        // in case something changes in the other parts of the program
        else if (args.getParameters().size() != procedure.getParamList().size()) {
            errors.add(String.format("%s: Incorrect number of arguments in call to procedure %s",
                                     getPosition(),
                                     procedure.getName()));
        } else {
            for (int i = 0; i < args.getParameters().size(); i++) {
                if (!args.getParameters()
                         .get(i)
                         .getReturnedType()
                         .equals(procedure.getParamList().get(i).getType())) {
                    errors.add(String.format("%s: Type mismatch in arg #%d in call to procedure %s",
                                             args.getParameters().get(i).getPosition(),
                                             i + 1,
                                             procedure.getName()));
                }
            }
        }
        return errors;
    }

    /**
     * @return a list of all errors, both semantic and syntactical ones.
     */
    @NotNull
    @Override
    public List<String> getAllErrors() {
        return collectErrorsFrom(args, getNext());
    }

    /**
     * Two ProcedureCallNodes are considered equal if their procedures and args are non-null and are
     * equal to each other respectively.
     */
    @Override
    public boolean equals(@NotNull final Object obj) {
        if (!(obj instanceof ProcedureCallNode)) {
            return false;
        }
        final ProcedureCallNode other = (ProcedureCallNode) obj;
        return procedure.equals(other.procedure) && args.equals(other.args);
    }

    @Override
    public int getID() {
        return procedure.getID();
    }

    /**
     * @return dot/graphviz declarations of this component's children
     */
    @NotNull
    @Override
    public String getDotRepresentation() {
        final StringBuilder dotRepr = new StringBuilder(super.getDotRepresentation());
        dotRepr.append(System.lineSeparator());

        // define this node
        dotRepr.append(String.format("%d[label=\"%s\"];", procedure.getID(), procedure.getName()));

        // define links to next
        if (getNext() != null) {
            dotRepr.append(String.format("%d -> %d;", getID(), getNext().getID()));
            dotRepr.append(System.lineSeparator());
        }

        return dotRepr.toString();
    }

    @NotNull
    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void transformToSSA(final @NotNull BaseBlock baseBlock) {
        transformToSSA(baseBlock, null);
    }

    @Override
    public void transformToSSA(final @NotNull BaseBlock baseBlock,
                               final @Nullable JoinBlock joinBlock) {
        final List<Expression> parameters = args.getParameters();
        final Operand[] operands = new Operand[3 + parameters.size()];
        operands[0] = new SymbolicOperand(procedure.getClassName());
        operands[1] = new SymbolicOperand(procedure.getName());
        operands[2] = new SymbolicOperand("this");
        for (int i = 0; i < parameters.size(); i++) {
            if (parameters.get(i) instanceof SSATransformableExpression) {
                final SSATransformableExpression param =
                        (SSATransformableExpression) parameters.get(i);
                param.transformToSSA(baseBlock);
                operands[3 + i] = param.getOperand();
            }
        }
        final Instruction instruction =
                new SpecialInstruction(SpecialInstruction.Type.DISPATCH, operands);
        baseBlock.insert(instruction);
        this.operand = new InstructionOperand(instruction);

    }

    /**
     * @return the operand that this expression was transformed to
     */
    @Nullable
    @Override
    public Operand getOperand() {
        return operand;
    }

}
