/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.Map;

@AutoValue
public abstract class Titles {
    private static final String KEY_WIDGETS = "widget";
    private static final String KEY_QUERY = "tab";
    private static final String KEY_TITLE = "title";

    @JsonValue
    abstract Map<String, Map<String, String>> titles();

    public static Titles ofWidgetTitles(Map<String, String> titles) {
        return ofTitles(Collections.singletonMap(KEY_WIDGETS, titles));
    }

    static Titles ofTitles(Map<String, Map<String, String>> titles) {
        return new AutoValue_Titles(titles);
    }
}
