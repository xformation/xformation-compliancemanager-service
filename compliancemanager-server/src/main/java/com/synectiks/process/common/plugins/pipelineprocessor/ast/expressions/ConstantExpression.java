/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;

import java.util.Collections;

public abstract class ConstantExpression extends BaseExpression {

    private final Class type;

    protected ConstantExpression(Token start, Class type) {
        super(start);
        this.type = type;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public Class getType() {
        return type;
    }

    @Override
    public Iterable<Expression> children() {
        return Collections.emptySet();
    }
}
