/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

import java.util.Collections;

public class FieldRefExpression extends BaseExpression {
    private final String variableName;
    private final Expression fieldExpr;

    public FieldRefExpression(Token start, String variableName, Expression fieldExpr) {
        super(start);
        this.variableName = variableName;
        this.fieldExpr = fieldExpr;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public Object evaluateUnsafe(EvaluationContext context) {
        return variableName;
    }

    @Override
    public Class getType() {
        return String.class;
    }

    @Override
    public String toString() {
        return variableName;
    }

    public String fieldName() {
        return variableName;
    }

    @Override
    public Iterable<Expression> children() {
        return Collections.emptySet();
    }
}
