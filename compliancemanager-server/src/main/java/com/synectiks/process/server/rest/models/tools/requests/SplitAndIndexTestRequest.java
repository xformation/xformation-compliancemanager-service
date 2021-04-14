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
public abstract class SplitAndIndexTestRequest {
    @JsonProperty
    public abstract String string();

    @JsonProperty("split_by")
    public abstract String splitBy();

    @JsonProperty
    public abstract int index();

    @JsonCreator
    public static SplitAndIndexTestRequest create(@JsonProperty("string") String string,
                                                  @JsonProperty("split_by") String splitBy,
                                                  @JsonProperty("index") int index) {
        return new AutoValue_SplitAndIndexTestRequest(string, splitBy, index);
    }
}
