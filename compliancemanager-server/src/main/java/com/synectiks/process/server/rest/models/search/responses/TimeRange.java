/*
 * */
package com.synectiks.process.server.rest.models.search.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;
import org.joda.time.DateTime;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class TimeRange {
    @JsonProperty
    public abstract DateTime from();

    @JsonProperty
    public abstract DateTime to();

    @JsonCreator
    public static TimeRange create(@JsonProperty("from") DateTime from, @JsonProperty("to") DateTime to) {
        return new AutoValue_TimeRange(from, to);
    }
}
