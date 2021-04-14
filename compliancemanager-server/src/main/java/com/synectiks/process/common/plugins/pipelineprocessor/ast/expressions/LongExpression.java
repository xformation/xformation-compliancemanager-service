/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

public class LongExpression extends ConstantExpression implements NumericExpression {
    private final long value;

    public LongExpression(Token start, long value) {
        super(start, Long.class);
        this.value = value;
    }

    @Override
    public Object evaluateUnsafe(EvaluationContext context) {
        return value;
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

    @Override
    public boolean isIntegral() {
        return true;
    }

    @Override
    public long evaluateLong(EvaluationContext context) {
        return value;
    }

    @Override
    public double evaluateDouble(EvaluationContext context) {
        return value;
    }
}
