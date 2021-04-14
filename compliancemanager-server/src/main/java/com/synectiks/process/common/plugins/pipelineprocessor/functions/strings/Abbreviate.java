/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.strings;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import org.apache.commons.lang3.StringUtils;

import static com.google.common.primitives.Ints.saturatedCast;

public class Abbreviate extends AbstractFunction<String> {

    public static final String NAME = "abbreviate";
    private static final String VALUE = "value";
    private static final String WIDTH = "width";
    private final ParameterDescriptor<String, String> valueParam;
    private final ParameterDescriptor<Long, Long> widthParam;

    public Abbreviate() {
        valueParam = ParameterDescriptor.string(VALUE).description("The string to abbreviate").build();
        widthParam = ParameterDescriptor.integer(WIDTH).description("The maximum number of characters including the '...' (at least 4)").build();
    }

    @Override
    public String evaluate(FunctionArgs args, EvaluationContext context) {
        final String value = valueParam.required(args, context);
        final Long required = widthParam.required(args, context);
        if (required == null) {
            return null;
        }
        final Long maxWidth = Math.max(required, 4L);

        return StringUtils.abbreviate(value, saturatedCast(maxWidth));
    }

    @Override
    public FunctionDescriptor<String> descriptor() {
        ImmutableList.Builder<ParameterDescriptor> params = ImmutableList.builder();
        params.add();

        return FunctionDescriptor.<String>builder()
                .name(NAME)
                .returnType(String.class)
                .params(ImmutableList.of(
                        valueParam,
                        widthParam
                ))
                .description("Abbreviates a string by appending '...' to fit into a maximum amount of characters")
                .build();
    }
}
