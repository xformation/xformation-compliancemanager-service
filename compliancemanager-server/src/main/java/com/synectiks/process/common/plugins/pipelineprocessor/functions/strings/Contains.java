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

public class Contains extends AbstractFunction<Boolean> {

    public static final String NAME = "contains";
    private final ParameterDescriptor<String, String> valueParam;
    private final ParameterDescriptor<String, String> searchParam;
    private final ParameterDescriptor<Boolean, Boolean> ignoreCaseParam;

    public Contains() {
        valueParam = ParameterDescriptor.string("value").description("The string to check").build();
        searchParam = ParameterDescriptor.string("search").description("The substring to find").build();
        ignoreCaseParam = ParameterDescriptor.bool("ignore_case").optional().description("Whether to search case insensitive, defaults to false").build();
    }

    @Override
    public Boolean evaluate(FunctionArgs args, EvaluationContext context) {
        final String value = valueParam.required(args, context);
        final String search = searchParam.required(args, context);
        final boolean ignoreCase = ignoreCaseParam.optional(args, context).orElse(false);
        if (ignoreCase) {
            return StringUtils.containsIgnoreCase(value, search);
        } else {
            return StringUtils.contains(value, search);
        }
    }

    @Override
    public FunctionDescriptor<Boolean> descriptor() {
        return FunctionDescriptor.<Boolean>builder()
                .name(NAME)
                .returnType(Boolean.class)
                .params(of(
                        valueParam,
                        searchParam,
                        ignoreCaseParam
                ))
                .description("Checks if a string contains a substring")
                .build();
    }
}
