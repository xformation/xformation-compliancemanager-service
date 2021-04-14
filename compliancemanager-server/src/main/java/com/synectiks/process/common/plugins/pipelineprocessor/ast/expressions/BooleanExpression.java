/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

public class BooleanExpression extends ConstantExpression implements LogicalExpression {
    private final boolean value;

    public BooleanExpression(Token start, boolean value) {
        super(start, Boolean.class);
        this.value = value;
    }

    @Override
    public Object evaluateUnsafe(EvaluationContext context) {
        return value;
    }


    @Override
    public boolean evaluateBool(EvaluationContext context) {
        return value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
