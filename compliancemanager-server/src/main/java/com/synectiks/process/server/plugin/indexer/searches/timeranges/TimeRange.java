/*
 * */
package com.synectiks.process.server.plugin.indexer.searches.timeranges;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.joda.time.DateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(name = AbsoluteRange.ABSOLUTE, value = AbsoluteRange.class),
        @JsonSubTypes.Type(name = RelativeRange.RELATIVE, value = RelativeRange.class),
        @JsonSubTypes.Type(name = KeywordRange.KEYWORD, value = KeywordRange.class)
})
public abstract class TimeRange {

    @JsonProperty
    public abstract String type();

    @JsonIgnore
    public abstract DateTime getFrom();

    @JsonIgnore
    public abstract DateTime getTo();
}
