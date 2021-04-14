/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

public class AndExpression extends BinaryExpression implements LogicalExpression {
    public AndExpression(Token start, Expression left,
                         Expression right) {
        super(start, left, right);
    }

    @Override
    public Object evaluateUnsafe(EvaluationContext context) {
        return evaluateBool(context);
    }

    @Override
    public boolean evaluateBool(EvaluationContext context) {
        return ((LogicalExpression)left).evaluateBool(context) && ((LogicalExpression)right).evaluateBool(context);
    }

    @Override
    public Class getType() {
        return Boolean.class;
    }

    @Override
    public String toString() {
        return left.toString() + " AND " + right.toString();
    }
}
