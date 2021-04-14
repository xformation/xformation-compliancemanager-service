/*
 * */
package com.synectiks.process.server.rest.models.system;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class DisplayGettingStarted {

    @JsonProperty("show")
    public abstract boolean show();

    @JsonCreator
    public static DisplayGettingStarted create(@JsonProperty("show") boolean show) {
        return new AutoValue_DisplayGettingStarted(show);
    }
}
