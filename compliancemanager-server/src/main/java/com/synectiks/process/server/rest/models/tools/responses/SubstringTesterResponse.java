/*
 * */
package com.synectiks.process.server.rest.models.tools.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class SubstringTesterResponse {
    @JsonProperty
    public abstract boolean successful();

    @JsonProperty
    @Nullable
    public abstract String cut();

    @JsonProperty("begin_index")
    public abstract int beginIndex();

    @JsonProperty("end_index")
    public abstract int endIndex();

    @JsonCreator
    public static SubstringTesterResponse create(@JsonProperty("successful")boolean successful,
                                                 @JsonProperty("cut") @Nullable String cut,
                                                 @JsonProperty("begin_index") int beginIndex,
                                                 @JsonProperty("end_index")int endIndex) {
        return new AutoValue_SubstringTesterResponse(successful, cut, beginIndex, endIndex);
    }
}
