/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

public interface NumericExpression extends Expression {

    boolean isIntegral();

    long evaluateLong(EvaluationContext context);

    double evaluateDouble(EvaluationContext context);
}
