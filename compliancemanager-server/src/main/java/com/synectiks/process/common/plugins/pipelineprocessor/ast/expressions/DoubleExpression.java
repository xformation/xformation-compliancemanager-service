/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

public class DoubleExpression extends ConstantExpression implements NumericExpression {
    private final double value;

    public DoubleExpression(Token start, double value) {
        super(start, Double.class);
        this.value = value;
    }

    @Override
    public Object evaluateUnsafe(EvaluationContext context) {
        return value;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    @Override
    public boolean isIntegral() {
        return false;
    }

    @Override
    public long evaluateLong(EvaluationContext context) {
        return (long) value;
    }

    @Override
    public double evaluateDouble(EvaluationContext context) {
        return value;
    }
}
