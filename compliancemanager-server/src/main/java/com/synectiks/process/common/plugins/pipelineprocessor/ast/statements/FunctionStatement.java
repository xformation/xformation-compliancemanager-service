/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.statements;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.Expression;

public class FunctionStatement implements Statement {

    private final Expression functionExpression;

    public FunctionStatement(Expression functionExpression) {
        this.functionExpression = functionExpression;
    }

    @Override
    public Object evaluate(EvaluationContext context) {
        return functionExpression.evaluate(context);
    }

    public Expression getFunctionExpression() {
        return functionExpression;
    }

    @Override
    public String toString() {
        return functionExpression.toString();
    }
}
