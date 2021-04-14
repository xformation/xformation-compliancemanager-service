/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.exceptions;

import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.FunctionExpression;

public class FunctionEvaluationException extends LocationAwareEvalException {
    private final FunctionExpression functionExpression;
    private final Exception exception;

    public FunctionEvaluationException(FunctionExpression functionExpression, Exception exception) {
        super(functionExpression.getStartToken(), exception);
        this.functionExpression = functionExpression;
        this.exception = exception;
    }

    public FunctionExpression getFunctionExpression() {
        return functionExpression;
    }

    public Exception getException() {
        return exception;
    }
}
