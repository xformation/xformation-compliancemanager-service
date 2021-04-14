/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;

public abstract class BaseExpression implements Expression {

    private final Token startToken;

    public BaseExpression(Token startToken) {
        this.startToken = startToken;
    }

    @Override
    public Token getStartToken() {
        return startToken;
    }

}
