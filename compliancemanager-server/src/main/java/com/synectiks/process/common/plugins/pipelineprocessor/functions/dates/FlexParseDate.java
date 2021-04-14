/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.dates;

import com.google.common.collect.ImmutableList;
import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;
import java.util.Optional;

public class FlexParseDate extends TimezoneAwareFunction {

    public static final String VALUE = "value";
    public static final String NAME = "flex_parse_date";
    public static final String DEFAULT = "default";
    private final ParameterDescriptor<String, String> valueParam;
    private final ParameterDescriptor<DateTime, DateTime> defaultParam;

    public FlexParseDate() {
        valueParam = ParameterDescriptor.string(VALUE).description("Date string to parse").build();
        defaultParam = ParameterDescriptor.type(DEFAULT, DateTime.class).optional().description("Used when 'value' could not be parsed, 'null' otherwise").build();
    }

    @Override
    protected DateTime evaluate(FunctionArgs args, EvaluationContext context, DateTimeZone timezone) {
        final String time = valueParam.required(args, context);

        final List<DateGroup> dates = new Parser(timezone.toTimeZone()).parse(time);
        if (dates.size() == 0) {
            final Optional<DateTime> defaultTime = defaultParam.optional(args, context);
            if (defaultTime.isPresent()) {
                return defaultTime.get();
            }
            // TODO really? this should probably throw an exception of some sort to be handled in the interpreter
            return null;
        }
        return new DateTime(dates.get(0).getDates().get(0), timezone);
    }

    @Override
    protected String description() {
        return "Parses a date string using natural language (see http://natty.joestelmach.com/)";
    }

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected ImmutableList<ParameterDescriptor> params() {
        return ImmutableList.of(
                valueParam,
                defaultParam
        );
    }
}
