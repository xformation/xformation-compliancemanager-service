/*
 * */
package com.synectiks.process.server.rest.models.count.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class MessageCountResponse {
    @JsonProperty
    public abstract long events();

    public static MessageCountResponse create(long events) {
        return new AutoValue_MessageCountResponse(events);
    }
}
