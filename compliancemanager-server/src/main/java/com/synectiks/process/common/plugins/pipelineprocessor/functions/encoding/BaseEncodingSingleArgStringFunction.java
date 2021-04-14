/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.encoding;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import java.util.Locale;

import static com.google.common.collect.ImmutableList.of;

abstract class BaseEncodingSingleArgStringFunction extends AbstractFunction<String> {

    private final ParameterDescriptor<String, String> valueParam;
    private final ParameterDescriptor<Boolean, Boolean> omitPaddingParam;

    BaseEncodingSingleArgStringFunction() {
        valueParam = ParameterDescriptor.string("value").description("The value to encode with " + getEncodingName()).build();
        omitPaddingParam = ParameterDescriptor.bool("omit_padding").optional().description("Omit any padding characters as specified by RFC 4648 section 3.2").build();
    }

    @Override
    public String evaluate(FunctionArgs args, EvaluationContext context) {
        final String value = valueParam.required(args, context);
        final boolean omitPadding = omitPaddingParam.optional(args, context).orElse(false);
        return getEncodedValue(value, omitPadding);
    }

    protected abstract String getEncodedValue(String value, boolean omitPadding);

    protected abstract String getName();

    protected abstract String getEncodingName();

    protected String description() {
        return getEncodingName() + " encoding/decoding of the string";
    }

    @Override
    public FunctionDescriptor<String> descriptor() {
        return FunctionDescriptor.<String>builder()
                .name(getName())
                .returnType(String.class)
                .params(of(valueParam, omitPaddingParam))
                .description(description())
                .build();
    }
}
