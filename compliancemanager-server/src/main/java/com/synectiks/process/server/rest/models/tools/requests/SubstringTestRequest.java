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
public abstract class SubstringTestRequest {
    @JsonProperty
    public abstract String string();

    @JsonProperty
    public abstract int start();

    @JsonProperty
    public abstract int end();

    @JsonCreator
    public static SubstringTestRequest create(@JsonProperty("string") String string,
                                              @JsonProperty("start") int start,
                                              @JsonProperty("end") int end) {
        return new AutoValue_SubstringTestRequest(string, start, end);
    }
}
