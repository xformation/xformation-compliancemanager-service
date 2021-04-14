/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.strings;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class Capitalize extends StringUtilsFunction {

    public static final String NAME = "capitalize";

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected String description() {
        return "Capitalizes a String changing the first letter to title case from lower case";
    }

    @Override
    protected boolean isLocaleAware() {
        return false;
    }

    @Override
    protected String apply(String value, Locale unused) {
        return StringUtils.capitalize(value);
    }
}
