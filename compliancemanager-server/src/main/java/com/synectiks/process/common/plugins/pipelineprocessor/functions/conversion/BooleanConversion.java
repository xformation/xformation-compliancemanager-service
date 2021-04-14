/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion;

import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.bool;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.object;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import static com.google.common.collect.ImmutableList.of;

public class BooleanConversion extends AbstractFunction<Boolean> {
    public static final String NAME = "to_bool";

    private final ParameterDescriptor<Object, Object> valueParam;
    private final ParameterDescriptor<Boolean, Boolean> defaultParam;


    public BooleanConversion() {
        valueParam = object("value").description("Value to convert").build();
        defaultParam = bool("default").optional().description("Used when 'value' is null, defaults to false").build();
    }

    @Override
    public Boolean evaluate(FunctionArgs args, EvaluationContext context) {
        final Object value = valueParam.required(args, context);
        if (value == null) {
            return defaultParam.optional(args, context).orElse(false);
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    @Override
    public FunctionDescriptor<Boolean> descriptor() {
        return FunctionDescriptor.<Boolean>builder()
                .name(NAME)
                .returnType(Boolean.class)
                .params(of(valueParam, defaultParam))
                .description("Converts a value to a boolean value using its string representation")
                .build();
    }
}
