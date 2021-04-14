/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;

import com.synectiks.process.common.plugins.pipelineprocessor.parser.ParseException;
import com.synectiks.process.common.plugins.pipelineprocessor.parser.errors.SyntaxError;

import java.util.Collections;

public abstract class UnaryExpression extends BaseExpression {

    protected Expression right;

    public UnaryExpression(Token start, Expression right) {
        super(start);
        this.right = requireNonNull(right, start);
    }

    private static Expression requireNonNull(Expression expression, Token token) {
        if (expression != null) {
            return expression;
        } else {
            final int line = token.getLine();
            final int positionInLine = token.getCharPositionInLine();
            final String msg = "Invalid expression (line: " + line + ", column: " + positionInLine + ")";
            final SyntaxError syntaxError = new SyntaxError(token.getText(), line, positionInLine, msg, null);
            throw new ParseException(Collections.singleton(syntaxError));
        }
    }

    @Override
    public boolean isConstant() {
        return right.isConstant();
    }

    @Override
    public Class getType() {
        return right.getType();
    }

    public Expression right() {
        return right;
    }

    public void right(Expression right) {
        this.right = right;
    }

    @Override
    public Iterable<Expression> children() {
        return Collections.singleton(right);
    }
}
