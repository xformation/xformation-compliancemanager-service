/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.strings;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class Uppercase extends StringUtilsFunction {

    public static final String NAME = "uppercase";

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected String description() {
        return "Uppercases a string";
    }

    @Override
    protected boolean isLocaleAware() {
        return true;
    }

    @Override
    protected String apply(String value, Locale locale) {
        return StringUtils.upperCase(value, locale);
    }
}
