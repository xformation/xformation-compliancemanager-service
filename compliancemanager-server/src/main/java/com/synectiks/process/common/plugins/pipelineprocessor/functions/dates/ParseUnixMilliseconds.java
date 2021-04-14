/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.dates;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class ParseUnixMilliseconds extends TimezoneAwareFunction {
    public static final String NAME = "parse_unix_milliseconds";

    private static final String VALUE = "value";

    private final ParameterDescriptor<Long, Long> valueParam;

    public ParseUnixMilliseconds() {
        valueParam = ParameterDescriptor.integer(VALUE).description("UNIX millisecond timestamp to parse").build();
    }

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected ImmutableList<ParameterDescriptor> params() {
        return ImmutableList.of(valueParam);
    }

    @Override
    public DateTime evaluate(FunctionArgs args, EvaluationContext context, DateTimeZone timezone) {
        final Long unixMillis = valueParam.required(args, context);
        return unixMillis == null ? null : new DateTime(unixMillis, timezone);
    }

    @Override
    protected String description() {
        return "Converts a UNIX millisecond timestamp into a date";
    }
}
