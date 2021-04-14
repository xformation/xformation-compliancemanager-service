/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.strings;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Length extends AbstractFunction<Long> {

    public static final String NAME = "length";
    private static final String VALUE = "value";
    private static final String BYTES = "bytes";
    private final ParameterDescriptor<String, String> valueParam;
    private final ParameterDescriptor<Boolean, Boolean> bytesParam;

    public Length() {
        valueParam = ParameterDescriptor.string(VALUE).description("The input string").build();
        bytesParam = ParameterDescriptor.bool(BYTES)
                .description("If true, count the bytes of the UTF-8 string instead of the characters")
                .optional().build();
    }

    @Override
    public Long evaluate(FunctionArgs args, EvaluationContext context) {
        final String string = Objects.requireNonNull(valueParam.required(args, context));
        final Boolean bytesFlag = bytesParam.optional(args, context).orElse(Boolean.FALSE);
        if (bytesFlag) {
            return (long) string.getBytes(StandardCharsets.UTF_8).length;
        }
        return (long) string.length();
    }

    @Override
    public FunctionDescriptor<Long> descriptor() {
        return FunctionDescriptor.<Long>builder()
                .name(NAME)
                .returnType(Long.class)
                .params(ImmutableList.of(valueParam, bytesParam))
                .description("Counts the characters or bytes in a string")
                .build();
    }
}
