/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.strings;

import com.google.common.base.Strings;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import static com.google.common.collect.ImmutableList.of;

public class Concat extends AbstractFunction<String> {
    public static final String NAME = "concat";
    private final ParameterDescriptor<String, String> firstParam;
    private final ParameterDescriptor<String, String> secondParam;

    public Concat() {
        firstParam = ParameterDescriptor.string("first").description("First string").build();
        secondParam = ParameterDescriptor.string("second").description("Second string").build();
    }

    @Override
    public String evaluate(FunctionArgs args, EvaluationContext context) {
        final String first = Strings.nullToEmpty(firstParam.required(args, context));
        final String second = Strings.nullToEmpty(secondParam.required(args, context));

        return first.concat(second);
    }

    @Override
    public FunctionDescriptor<String> descriptor() {
        return FunctionDescriptor.<String>builder()
                .name(NAME)
                .returnType(String.class)
                .params(of(firstParam, secondParam))
                .description("Concatenates two strings")
                .build();
    }
}
