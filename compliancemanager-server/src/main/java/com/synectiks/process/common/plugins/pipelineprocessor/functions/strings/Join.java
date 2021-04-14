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

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Join extends AbstractFunction<String> {
    public static final String NAME = "join";

    private final ParameterDescriptor<String, String> delimiterParam;
    private final ParameterDescriptor<Object, List> elementsParam;
    private final ParameterDescriptor<Long, Integer> startIndexParam;
    private final ParameterDescriptor<Long, Integer> endIndexParam;

    public Join() {
        elementsParam = ParameterDescriptor.type("elements", Object.class, List.class)
                .transform(Join::toList)
                .description("The list of strings to join together, may be null")
                .build();
        delimiterParam = ParameterDescriptor.string("delimiter").optional()
                .description("The delimiter that separates each element. Default: none")
                .build();
        startIndexParam = ParameterDescriptor.integer("start", Integer.class).optional()
                .transform(Ints::saturatedCast)
                .description("The first index to start joining from. It is an error to pass in an index larger than the number of elements")
                .build();
        endIndexParam = ParameterDescriptor.integer("end", Integer.class).optional()
                .transform(Ints::saturatedCast)
                .description("The index to stop joining from (exclusive). It is an error to pass in an index larger than the number of elements")
                .build();
    }

    private static List toList(Object obj) {
        if (obj instanceof Collection) {
            return ImmutableList.copyOf((Collection) obj);
        } else {
            throw new IllegalArgumentException("Unsupported data type for parameter 'elements': " + obj.getClass().getCanonicalName());
        }
    }

    @Override
    public String evaluate(FunctionArgs args, EvaluationContext context) {
        final List elements = elementsParam.optional(args, context).orElse(Collections.emptyList());
        final int length = elements.size();

        final String delimiter = delimiterParam.required(args, context);
        final int startIndex = startIndexParam.optional(args, context).filter(idx -> idx >= 0).orElse(0);
        final int endIndex = endIndexParam.optional(args, context).filter(idx -> idx >= 0).orElse(length);

        return StringUtils.join(elements.subList(startIndex, endIndex), delimiter);
    }

    @Override
    public FunctionDescriptor<String> descriptor() {
        return FunctionDescriptor.<String>builder()
                .name(NAME)
                .pure(true)
                .returnType(String.class)
                .params(ImmutableList.of(elementsParam, delimiterParam, startIndexParam, endIndexParam))
                .description("Joins the elements of the provided array into a single String")
                .build();
    }
}
