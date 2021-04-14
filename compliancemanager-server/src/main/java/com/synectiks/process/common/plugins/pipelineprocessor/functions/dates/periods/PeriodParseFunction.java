/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.periods;

import org.joda.time.Period;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

public class PeriodParseFunction extends AbstractFunction<Period> {

    public static final String NAME = "period";
    private final ParameterDescriptor<String, Period> value =
            ParameterDescriptor
                    .string("value", Period.class)
                    .transform(Period::parse)
                    .build();


    @Override
    public Period evaluate(FunctionArgs args, EvaluationContext context) {
        return value.required(args, context);
    }

    @Override
    public FunctionDescriptor<Period> descriptor() {
        return FunctionDescriptor.<Period>builder()
                .name(NAME)
                .description("Parses a ISO 8601 period from the specified string.")
                .pure(true)
                .returnType(Period.class)
                .params(value)
                .build();
    }
}
