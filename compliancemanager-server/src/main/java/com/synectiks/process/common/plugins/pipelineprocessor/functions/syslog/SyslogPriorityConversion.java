/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.syslog;

import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.object;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

public class SyslogPriorityConversion extends AbstractFunction<SyslogPriority> {
    public static final String NAME = "expand_syslog_priority";

    private final ParameterDescriptor<Object, Object> valueParam = object("value").description("Value to convert").build();

    @Override
    public SyslogPriority evaluate(FunctionArgs args, EvaluationContext context) {
        final String s = String.valueOf(valueParam.required(args, context));
        final int priority = Integer.parseInt(s);
        final int facility = SyslogUtils.facilityFromPriority(priority);
        final int level = SyslogUtils.levelFromPriority(priority);

        return SyslogPriority.create(level, facility);
    }

    @Override
    public FunctionDescriptor<SyslogPriority> descriptor() {
        return FunctionDescriptor.<SyslogPriority>builder()
                .name(NAME)
                .returnType(SyslogPriority.class)
                .params(valueParam)
                .description("Converts a syslog priority number to its level and facility")
                .build();
    }
}
