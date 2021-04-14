/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.stream.Collectors;

public class ArrayLiteralExpression extends BaseExpression {
    private final List<Expression> elements;

    public ArrayLiteralExpression(Token start, List<Expression> elements) {
        super(start);
        this.elements = elements;
    }

    @Override
    public boolean isConstant() {
        return elements.stream().allMatch(Expression::isConstant);
    }

    @Override
    public List evaluateUnsafe(EvaluationContext context) {
        return  elements.stream()
                .map(expression -> expression.evaluateUnsafe(context))
                .collect(Collectors.toList());
    }

    @Override
    public Class getType() {
        return List.class;
    }

    @Override
    public String toString() {
        return "[" + Joiner.on(", ").join(elements) + "]";
    }

    @Override
    public Iterable<Expression> children() {
        return ImmutableList.copyOf(elements);
    }
}
