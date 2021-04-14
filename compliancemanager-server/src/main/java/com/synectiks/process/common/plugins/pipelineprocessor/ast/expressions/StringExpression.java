/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

public class StringExpression extends ConstantExpression {

    private final String value;

    public StringExpression(Token start, String value) {
        super(start, String.class);
        this.value = value;
    }

    @Override
    public Object evaluateUnsafe(EvaluationContext context) {
        return value;
    }

    @Override
    public String toString() {
        return '"' + value + '"';
    }
}
