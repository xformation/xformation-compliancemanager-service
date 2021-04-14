/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.strings;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import java.util.Locale;

public abstract class StringUtilsFunction extends AbstractFunction<String> {

    private static final String VALUE = "value";
    private static final String LOCALE = "locale";
    private final ParameterDescriptor<String, String> valueParam;
    private final ParameterDescriptor<String, Locale> localeParam;

    public StringUtilsFunction() {
        valueParam = ParameterDescriptor.string(VALUE).description("The input string").build();
        localeParam = ParameterDescriptor.string(LOCALE, Locale.class)
                .optional()
                .transform(Locale::forLanguageTag)
                .description("The locale to use, defaults to English")
                .build();
    }

    @Override
    public String evaluate(FunctionArgs args, EvaluationContext context) {
        final String value = valueParam.required(args, context);
        Locale locale = Locale.ENGLISH;
        if (isLocaleAware()) {
            locale = localeParam.optional(args, context).orElse(Locale.ENGLISH);
        }
        return apply(value, locale);
    }

    @Override
    public FunctionDescriptor<String> descriptor() {
        ImmutableList.Builder<ParameterDescriptor> params = ImmutableList.builder();
        params.add(valueParam);
        if (isLocaleAware()) {
            params.add(localeParam);
        }
        return FunctionDescriptor.<String>builder()
                .name(getName())
                .returnType(String.class)
                .params(params.build())
                .description(description())
                .build();
    }

    protected abstract String getName();

    protected abstract String description();

    protected abstract boolean isLocaleAware();

    protected abstract String apply(String value, Locale locale);
}
