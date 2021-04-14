/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.search.SeriesSpec;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoValue
public abstract class Series {
    private static final String FIELD_CONFIG = "config";
    private static final String FIELD_FUNCTION = "function";

    private static final Pattern destructuringPattern = Pattern.compile("(\\w+)\\((\\w+)?\\)");

    @JsonProperty(FIELD_CONFIG)
    public Map<String, Object> config() {
        return Collections.emptyMap();
    }

    @JsonProperty(FIELD_FUNCTION)
    public String function() {
        return "count()";
    }

    public static Series create() {
        return new AutoValue_Series();
    }

    public SeriesSpec toSeriesSpec() {
        final Matcher matcher = destructuringPattern.matcher(function());
        if (matcher.matches()) {
            final String functionName = matcher.group(1);
            final String optionalFieldName = matcher.group(2);
            return SeriesSpec.create(functionName, function(), optionalFieldName);
        }

        throw new RuntimeException("Unable to parse function: " + function());
    }
}
