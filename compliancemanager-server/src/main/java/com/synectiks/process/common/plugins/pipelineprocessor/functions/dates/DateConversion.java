/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.dates;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.time.ZonedDateTime;
import java.util.Date;

public class DateConversion extends TimezoneAwareFunction {

    public static final String NAME = "to_date";
    private final ParameterDescriptor<Object, Object> value;

    public DateConversion() {
        value = ParameterDescriptor.object("value").description("The value to convert to a date").build();
    }

    @Override
    protected DateTime evaluate(FunctionArgs args, EvaluationContext context, DateTimeZone timezone) {
        final Object datish = value.required(args, context);
        if (datish instanceof DateTime) {
            return (DateTime) datish;
        }
        if (datish instanceof Date) {
            return new DateTime(datish);
        }
        if (datish instanceof ZonedDateTime) {
            final ZonedDateTime zonedDateTime = (ZonedDateTime) datish;
            final DateTimeZone timeZone = DateTimeZone.forID(zonedDateTime.getZone().getId());
            return new DateTime(zonedDateTime.toInstant().toEpochMilli(), timeZone);
        }
        return null;
    }

    @Override
    protected String description() {
        return "Converts a type to a date, useful for $message.timestamp or related message fields.";
    }

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected ImmutableList<ParameterDescriptor> params() {
        return ImmutableList.of(value);
    }
}
