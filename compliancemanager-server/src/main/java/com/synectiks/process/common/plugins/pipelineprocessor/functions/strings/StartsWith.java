/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.strings;

import org.apache.commons.lang3.StringUtils;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import static com.google.common.collect.ImmutableList.of;

public class StartsWith extends AbstractFunction<Boolean> {

    public static final String NAME = "starts_with";
    private final ParameterDescriptor<String, String> valueParam;
    private final ParameterDescriptor<String, String> prefixParam;
    private final ParameterDescriptor<Boolean, Boolean> ignoreCaseParam;

    public StartsWith() {
        valueParam = ParameterDescriptor.string("value").description("The string to check").build();
        prefixParam = ParameterDescriptor.string("prefix").description("The prefix to check").build();
        ignoreCaseParam = ParameterDescriptor.bool("ignore_case").optional().description("Whether to search case insensitive, defaults to false").build();
    }

    @Override
    public Boolean evaluate(FunctionArgs args, EvaluationContext context) {
        final String value = valueParam.required(args, context);
        final String prefix = prefixParam.required(args, context);
        final boolean ignoreCase = ignoreCaseParam.optional(args, context).orElse(false);
        if (ignoreCase) {
            return StringUtils.startsWithIgnoreCase(value, prefix);
        } else {
            return StringUtils.startsWith(value, prefix);
        }
    }

    @Override
    public FunctionDescriptor<Boolean> descriptor() {
        return FunctionDescriptor.<Boolean>builder()
                .name(NAME)
                .returnType(Boolean.class)
                .params(of(
                        valueParam,
                        prefixParam,
                        ignoreCaseParam
                ))
                .description("Checks if a string starts with a prefix")
                .build();
    }
}
