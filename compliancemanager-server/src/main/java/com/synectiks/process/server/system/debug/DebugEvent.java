/*
 * */
package com.synectiks.process.server.system.debug;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class DebugEvent {
    @JsonProperty
    public abstract String nodeId();

    @JsonProperty
    public abstract DateTime date();

    @JsonProperty
    public abstract String text();

    @JsonCreator
    public static DebugEvent create(@JsonProperty("node_id") String nodeId,
                                    @JsonProperty("date") DateTime date,
                                    @JsonProperty("text") String text) {
        return new AutoValue_DebugEvent(nodeId, date, text);
    }

    public static DebugEvent create(String nodeId, String text) {
        return create(nodeId, DateTime.now(DateTimeZone.UTC), text);
    }
}
