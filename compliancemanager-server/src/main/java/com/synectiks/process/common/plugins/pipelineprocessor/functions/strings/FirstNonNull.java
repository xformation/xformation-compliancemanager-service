/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.strings;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import java.util.List;
import java.util.Objects;

public class FirstNonNull extends AbstractFunction<Object> {
    public static final String NAME = "first_non_null";

    private final ParameterDescriptor<List, List> valueParam;

    public FirstNonNull() {
        valueParam = ParameterDescriptor.type("value", List.class, List.class)
                .description("The list of fields to find first non null value")
                .build();
    }

    @Override
    public Object evaluate(FunctionArgs args, EvaluationContext context) {
        List elements = valueParam.required(args, context);
        return elements.stream().filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public FunctionDescriptor<Object> descriptor() {
        return FunctionDescriptor.builder()
                .name(NAME)
                .pure(false)
                .returnType(Object.class)
                .params(ImmutableList.of(valueParam))
                .description("Returns first non null element found in value")
                .build();
    }
}
