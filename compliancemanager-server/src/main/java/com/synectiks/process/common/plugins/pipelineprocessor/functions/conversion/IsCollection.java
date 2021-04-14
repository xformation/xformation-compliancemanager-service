/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.object;

import java.util.Collection;

public class IsCollection extends AbstractFunction<Boolean> {
    public static final String NAME = "is_collection";

    private final ParameterDescriptor<Object, Object> valueParam;

    public IsCollection() {
        valueParam = object("value").description("Value to check").build();
    }

    @Override
    public Boolean evaluate(FunctionArgs args, EvaluationContext context) {
        final Object value = valueParam.required(args, context);
        return value instanceof Collection;
    }

    @Override
    public FunctionDescriptor<Boolean> descriptor() {
        return FunctionDescriptor.<Boolean>builder()
                .name(NAME)
                .returnType(Boolean.class)
                .params(valueParam)
                .description("Checks whether a value is a collection")
                .build();
    }
}
