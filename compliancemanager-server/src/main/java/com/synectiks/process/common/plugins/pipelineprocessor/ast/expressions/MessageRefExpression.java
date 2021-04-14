/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

import java.util.Collections;

public class MessageRefExpression extends BaseExpression {
    private final Expression fieldExpr;

    public MessageRefExpression(Token start, Expression fieldExpr) {
        super(start);
        this.fieldExpr = fieldExpr;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public Object evaluateUnsafe(EvaluationContext context) {
        final Object fieldName = fieldExpr.evaluateUnsafe(context);
        if (fieldName == null) {
            return null;
        }
        return context.currentMessage().getField(fieldName.toString());
    }

    @Override
    public Class getType() {
        return Object.class;
    }

    @Override
    public String toString() {
        return "$message." + fieldExpr.toString();
    }

    public Expression getFieldExpr() {
        return fieldExpr;
    }

    @Override
    public Iterable<Expression> children() {
        return Collections.singleton(fieldExpr);
    }
}
