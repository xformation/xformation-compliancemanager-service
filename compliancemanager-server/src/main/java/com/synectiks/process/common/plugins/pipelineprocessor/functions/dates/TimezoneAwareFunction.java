/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.dates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Locale;

public abstract class TimezoneAwareFunction extends AbstractFunction<DateTime> {

    private static final String TIMEZONE = "timezone";
    private static final ImmutableMap<String, String> UPPER_ZONE_MAP = Maps.uniqueIndex(
            DateTimeZone.getAvailableIDs(),
            input -> input != null ? input.toUpperCase(Locale.ENGLISH) : "UTC");
    private final ParameterDescriptor<String, DateTimeZone> timeZoneParam;

    protected TimezoneAwareFunction() {
        timeZoneParam = ParameterDescriptor
                .string(TIMEZONE, DateTimeZone.class)
                .transform(id -> DateTimeZone.forID(UPPER_ZONE_MAP.getOrDefault(id.toUpperCase(Locale.ENGLISH), "UTC")))
                .optional()
                .description("The timezone to apply to the date, defaults to UTC")
                .build();
    }

    @Override
    public DateTime evaluate(FunctionArgs args, EvaluationContext context) {
        final DateTimeZone timezone = timeZoneParam.optional(args, context).orElse(DateTimeZone.UTC);

        return evaluate(args, context, timezone);
    }

    protected abstract DateTime evaluate(FunctionArgs args, EvaluationContext context, DateTimeZone timezone);

    @Override
    public FunctionDescriptor<DateTime> descriptor() {
        return FunctionDescriptor.<DateTime>builder()
                .name(getName())
                .returnType(DateTime.class)
                .params(ImmutableList.<ParameterDescriptor>builder()
                                .addAll(params())
                                .add(timeZoneParam)
                                .build())
                .description(description())
                .build();
    }

    protected abstract String description();

    protected abstract String getName();

    protected abstract ImmutableList<ParameterDescriptor> params();
}
