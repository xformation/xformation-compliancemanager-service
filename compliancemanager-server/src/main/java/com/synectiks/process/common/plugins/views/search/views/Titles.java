/*
 * */
package com.synectiks.process.common.plugins.views.search.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AutoValue
public abstract class Titles {
    private static final String KEY_WIDGETS = "widget";

    @JsonValue
    public abstract Map<String, Map<String, String>> titles();

    @JsonCreator
    public static Titles of(Map<String, Map<String, String>> titles) {
        return new AutoValue_Titles(titles);
    }

    public static Titles withWidgetTitle(Map<String, String> widgetTitleMap) {
        final Map<String, Map<String, String>> titlesMap = new HashMap<>(1);
        titlesMap.put(KEY_WIDGETS, widgetTitleMap);
        return of(titlesMap);
    }

    public static Titles empty() {
        return of(Collections.emptyMap());
    }

    public Optional<String> widgetTitle(String widgetId) {
        return Optional.ofNullable(titles().getOrDefault(KEY_WIDGETS, Collections.emptyMap()).get(widgetId));
    }
}
