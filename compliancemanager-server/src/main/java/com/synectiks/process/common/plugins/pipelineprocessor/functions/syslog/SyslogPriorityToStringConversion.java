/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.syslog;

import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.object;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

public class SyslogPriorityToStringConversion extends AbstractFunction<SyslogPriorityAsString> {
    public static final String NAME = "expand_syslog_priority_as_string";

    private final ParameterDescriptor<Object, Object> valueParam = object("value").description("Value to convert").build();

    @Override
    public SyslogPriorityAsString evaluate(FunctionArgs args, EvaluationContext context) {
        final String s = String.valueOf(valueParam.required(args, context));
        final int priority = Integer.parseInt(s);
        final int facility = SyslogUtils.facilityFromPriority(priority);
        final String facilityString = SyslogUtils.facilityToString(facility);
        final int level = SyslogUtils.levelFromPriority(priority);
        final String levelString = SyslogUtils.levelToString(level);

        return SyslogPriorityAsString.create(levelString, facilityString);
    }

    @Override
    public FunctionDescriptor<SyslogPriorityAsString> descriptor() {
        return FunctionDescriptor.<SyslogPriorityAsString>builder()
                .name(NAME)
                .returnType(SyslogPriorityAsString.class)
                .params(valueParam)
                .description("Converts a syslog priority number to its level and facility string representations")
                .build();
    }
}
