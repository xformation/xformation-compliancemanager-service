/*
 * */
package com.synectiks.process.server.rest.resources.streams.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class TestMatchResponse {
    @JsonProperty
    public abstract boolean matches();

    @JsonProperty
    public abstract Map<String, Boolean> rules();

    public static TestMatchResponse create(boolean matches, Map<String, Boolean> rules) {
        return new AutoValue_TestMatchResponse(matches, rules);
    }
}
