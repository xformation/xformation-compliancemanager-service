/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.strings;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import com.google.common.reflect.TypeToken;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public class Split extends AbstractFunction<List<String>> {
    @SuppressWarnings("unchecked")
    private static final Class<List<String>> RETURN_TYPE = (Class<List<String>>) new TypeToken<List<String>>() {
    }.getRawType();

    public static final String NAME = "split";

    private final ParameterDescriptor<String, Pattern> pattern;
    private final ParameterDescriptor<String, String> value;
    private final ParameterDescriptor<Long, Integer> limit;

    public Split() {
        pattern = ParameterDescriptor.string("pattern", Pattern.class)
                .transform(Pattern::compile)
                .description("The regular expression to split by, uses Java regex syntax")
                .build();
        value = ParameterDescriptor.string("value")
                .description("The string to be split")
                .build();
        limit = ParameterDescriptor.integer("limit", Integer.class)
                .transform(Ints::saturatedCast)
                .description("The number of times the pattern is applied")
                .optional()
                .build();
    }

    @Override
    public List<String> evaluate(FunctionArgs args, EvaluationContext context) {
        final Pattern regex = requireNonNull(pattern.required(args, context), "Argument 'pattern' cannot be 'null'");
        final String value = requireNonNull(this.value.required(args, context), "Argument 'value' cannot be 'null'");

        final int limit = this.limit.optional(args, context).orElse(0);
        checkArgument(limit >= 0, "Argument 'limit' cannot be negative");
        return ImmutableList.copyOf(regex.split(value, limit));
    }

    @Override
    public FunctionDescriptor<List<String>> descriptor() {
        return FunctionDescriptor.<List<String>>builder()
                .name(NAME)
                .pure(true)
                .returnType(RETURN_TYPE)
                .params(ImmutableList.of(pattern, value, limit))
                .description("Split a string around matches of this pattern (Java syntax)")
                .build();
    }
}
