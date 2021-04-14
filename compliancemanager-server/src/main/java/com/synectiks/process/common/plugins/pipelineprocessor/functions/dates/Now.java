/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.dates;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Now extends TimezoneAwareFunction {

    public static final String NAME = "now";

    @Override
    protected DateTime evaluate(FunctionArgs args, EvaluationContext context, DateTimeZone timezone) {
        return DateTime.now(timezone);
    }

    @Override
    protected String description() {
        return "Returns the current time";
    }

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected ImmutableList<ParameterDescriptor> params() {
        return ImmutableList.of();
    }
}
