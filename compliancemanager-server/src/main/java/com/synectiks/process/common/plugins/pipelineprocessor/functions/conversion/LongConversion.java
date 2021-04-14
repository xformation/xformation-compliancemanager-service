/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion;

import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.integer;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.object;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.primitives.Longs.tryParse;

public class LongConversion extends AbstractFunction<Long> {

    public static final String NAME = "to_long";

    private static final String VALUE = "value";
    private static final String DEFAULT = "default";

    private final ParameterDescriptor<Object, Object> valueParam;
    private final ParameterDescriptor<Long, Long> defaultParam;

    public LongConversion() {
        valueParam = object(VALUE).description("Value to convert").build();
        defaultParam = integer(DEFAULT).optional().description("Used when 'value' is null, defaults to 0").build();
    }

    @Override
    public Long evaluate(FunctionArgs args, EvaluationContext context) {
        final Object evaluated = valueParam.required(args, context);
        final Long defaultValue = defaultParam.optional(args, context).orElse(0L);

        if (evaluated == null) {
            return defaultValue;
        } else if (evaluated instanceof Number) {
            return ((Number) evaluated).longValue();
        } else {
            final String s = String.valueOf(evaluated);
            return firstNonNull(tryParse(s), defaultValue);
        }
    }

    @Override
    public FunctionDescriptor<Long> descriptor() {
        return FunctionDescriptor.<Long>builder()
                .name(NAME)
                .returnType(Long.class)
                .params(of(
                        valueParam,
                        defaultParam
                ))
                .description("Converts a value to a long value using its string representation")
                .build();
    }
}
