/*
 * */
package com.synectiks.process.server.rest.models.tools.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class GrokTestRequest {
    @JsonProperty
    public abstract String string();

    @JsonProperty
    public abstract String pattern();

    @JsonProperty("named_captures_only")
    public abstract boolean namedCapturesOnly();

    @JsonCreator
    public static GrokTestRequest create(@JsonProperty("string") String string,
                                         @JsonProperty("pattern") String pattern,
                                         @JsonProperty("named_captures_only") boolean namedCapturesOnly) {
        return new AutoValue_GrokTestRequest(string, pattern, namedCapturesOnly);
    }
}
