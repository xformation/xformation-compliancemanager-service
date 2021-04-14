/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;

public interface LogicalExpression extends Expression {

    boolean evaluateBool(EvaluationContext context);
}
