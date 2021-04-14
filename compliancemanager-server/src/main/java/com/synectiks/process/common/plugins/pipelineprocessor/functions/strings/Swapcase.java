/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.strings;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class Swapcase extends StringUtilsFunction {

    public static final String NAME = "swapcase";

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected String description() {
        return "Swaps the case of a String changing upper and title case to lower case, and lower case to upper case.";
    }

    @Override
    protected boolean isLocaleAware() {
        return false;
    }

    @Override
    protected String apply(String value, Locale unused) {
        return StringUtils.swapCase(value);
    }
}
