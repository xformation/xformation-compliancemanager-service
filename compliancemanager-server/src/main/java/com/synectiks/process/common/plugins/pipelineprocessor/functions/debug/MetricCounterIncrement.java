/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.debug;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import javax.inject.Inject;

public class MetricCounterIncrement extends AbstractFunction<Void> {

    public static final String NAME = "metric_counter_inc";
    private final ParameterDescriptor<String, Counter> nameParam;
    private final ParameterDescriptor<Long, Long> valueParam;

    @Inject
    public MetricCounterIncrement(MetricRegistry metricRegistry) {
        nameParam = ParameterDescriptor.string("name", Counter.class)
                .description("The counter metric name, will always be prefixed with 'org.graylog.rulemetrics.'")
                .transform(name -> metricRegistry.counter(MetricRegistry.name("org.alertmanager.rulemetrics", name)))
                .build();
        valueParam = ParameterDescriptor.integer("value")
                .description("Value to increment the counter by")
                .optional()
                .build();
    }

    @Override
    public Void evaluate(FunctionArgs args, EvaluationContext context) {
        final Counter counter = nameParam.required(args, context);
        final Long aLong = valueParam.optional(args, context).orElse(1L);
        //noinspection ConstantConditions
        counter.inc(aLong);
        return null;
    }

    @Override
    public FunctionDescriptor<Void> descriptor() {
        return FunctionDescriptor.<Void>builder()
                .name(NAME)
                .returnType(Void.class)
                .description("")
                .params(ImmutableList.of(nameParam, valueParam))
                .build();
    }
}
