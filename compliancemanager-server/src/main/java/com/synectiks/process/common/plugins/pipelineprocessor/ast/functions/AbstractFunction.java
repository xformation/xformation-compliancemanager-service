/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.ast.functions;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.expressions.Expression;

/**
 * Helper Function implementation which evaluates and memoizes all constant FunctionArgs.
 *
 * @param <T> the return type
 */
public abstract class AbstractFunction<T> implements Function<T> {

    @Override
    public Object preComputeConstantArgument(FunctionArgs args, String name, Expression arg) {
        return arg.evaluateUnsafe(EvaluationContext.emptyContext());
    }
}
