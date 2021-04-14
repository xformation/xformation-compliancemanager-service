/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AbsoluteRange.class, name = TimeRange.ABSOLUTE),
        @JsonSubTypes.Type(value = KeywordRange.class, name = TimeRange.KEYWORD),
        @JsonSubTypes.Type(value = RelativeRange.class, name = TimeRange.RELATIVE)
})
public abstract class TimeRange {
    public static final String ABSOLUTE = "absolute";
    public static final String KEYWORD = "keyword";
    public static final String RELATIVE = "relative";
    @JsonProperty
    public abstract String type();
}
