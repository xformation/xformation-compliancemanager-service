/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.strings;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class Uncapitalize extends StringUtilsFunction {

    public static final String NAME = "uncapitalize";

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected String description() {
        return "Uncapitalizes a String changing the first letter to lower case from title case";
    }

    @Override
    protected boolean isLocaleAware() {
        return false;
    }

    @Override
    protected String apply(String value, Locale unused) {
        return StringUtils.uncapitalize(value);
    }
}
