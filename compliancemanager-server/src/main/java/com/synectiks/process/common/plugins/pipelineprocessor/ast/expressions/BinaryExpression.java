/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import com.google.common.collect.ImmutableList;

import org.antlr.v4.runtime.Token;

public abstract class BinaryExpression extends UnaryExpression {

    protected Expression left;

    public BinaryExpression(Token start, Expression left, Expression right) {
        super(start, right);
        this.left = left;
    }

    @Override
    public boolean isConstant() {
        return left.isConstant() && right.isConstant();
    }

    public Expression left() {
        return left;
    }

    public void left(Expression left) {
        this.left = left;
    }
    @Override
    public Iterable<Expression> children() {
        return ImmutableList.of(left, right);
    }
}
