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

public class EndsWith extends AbstractFunction<Boolean> {

    public static final String NAME = "ends_with";
    private final ParameterDescriptor<String, String> valueParam;
    private final ParameterDescriptor<String, String> suffixParam;
    private final ParameterDescriptor<Boolean, Boolean> ignoreCaseParam;

    public EndsWith() {
        valueParam = ParameterDescriptor.string("value").description("The string to check").build();
        suffixParam = ParameterDescriptor.string("suffix").description("The suffix to check").build();
        ignoreCaseParam = ParameterDescriptor.bool("ignore_case").optional().description("Whether to search case insensitive, defaults to false").build();
    }

    @Override
    public Boolean evaluate(FunctionArgs args, EvaluationContext context) {
        final String value = valueParam.required(args, context);
        final String suffix = suffixParam.required(args, context);
        final boolean ignoreCase = ignoreCaseParam.optional(args, context).orElse(false);
        if (ignoreCase) {
            return StringUtils.endsWithIgnoreCase(value, suffix);
        } else {
            return StringUtils.endsWith(value, suffix);
        }
    }

    @Override
    public FunctionDescriptor<Boolean> descriptor() {
        return FunctionDescriptor.<Boolean>builder()
                .name(NAME)
                .returnType(Boolean.class)
                .params(of(
                        valueParam,
                        suffixParam,
                        ignoreCaseParam
                ))
                .description("Checks if a string ends with a suffix")
                .build();
    }
}
