/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

import java.util.Collections;

public class BooleanValuedFunctionWrapper extends BaseExpression implements LogicalExpression {
    private final Expression expr;

    public BooleanValuedFunctionWrapper(Token start, Expression expr) {
        super(start);
        this.expr = expr;
        if (!expr.getType().equals(Boolean.class)) {
            throw new IllegalArgumentException("expr must be of boolean type");
        }
    }

    @Override
    public boolean evaluateBool(EvaluationContext context) {
        final Object value = expr.evaluateUnsafe(context);
        return value != null && (Boolean) value;
    }

    @Override
    public boolean isConstant() {
        return expr.isConstant();
    }

    @Override
    public Object evaluateUnsafe(EvaluationContext context) {
        return evaluateBool(context);
    }

    @Override
    public Class getType() {
        return expr.getType();
    }

    public Expression expression() {
        return expr;
    }

    @Override
    public String toString() {
        return expr.toString();
    }

    @Override
    public Iterable<Expression> children() {
        return Collections.singleton(expr);
    }
}
