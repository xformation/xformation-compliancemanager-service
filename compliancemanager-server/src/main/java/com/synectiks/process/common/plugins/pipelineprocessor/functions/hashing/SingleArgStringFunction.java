/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing;

import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import java.util.Locale;

import static com.google.common.collect.ImmutableList.of;

abstract class SingleArgStringFunction extends AbstractFunction<String> {

    private final ParameterDescriptor<String, String> valueParam;

    SingleArgStringFunction() {
        valueParam = ParameterDescriptor.string("value").description("The value to hash").build();
    }

    @Override
    public String evaluate(FunctionArgs args, EvaluationContext context) {
        final String value = valueParam.required(args, context);
        return getDigest(value);
    }

    protected abstract String getDigest(String value);

    protected abstract String getName();

    protected String description() {
        return getName().toUpperCase(Locale.ENGLISH) + " hash of the string";
    }

    @Override
    public FunctionDescriptor<String> descriptor() {
        return FunctionDescriptor.<String>builder()
                .name(getName())
                .returnType(String.class)
                .params(of(
                        valueParam)
                )
                .description(description())
                .build();
    }
}
