/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.debug;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import static com.google.common.collect.ImmutableList.of;

public class Debug extends AbstractFunction<Void> {

    private final ParameterDescriptor<Object, Object> valueParam;

    public static final String NAME = "debug";

    Debug() {
        valueParam = ParameterDescriptor.object("value").description("The value to print in the alertmanager-server log.").build();
    }

    @Override
    public Void evaluate(FunctionArgs args, EvaluationContext context) {
        final Object value = valueParam.required(args, context);

        if(value == null) {
            log.info("PIPELINE DEBUG: Passed value is NULL.");
        } else {
            log.info("PIPELINE DEBUG: {}", value.toString());
        }

        return null;
    }

    @Override
    public FunctionDescriptor<Void> descriptor() {
        return FunctionDescriptor.<Void>builder()
                .name(NAME)
                .returnType(Void.class)
                .params(of(valueParam) )
                .description("Print any passed value as string in the alertmanager-server log. Note that this will only appear in the " +
                        "log of the graylog-server node that is processing the message you are trying to debug.")
                .build();
    }

}
