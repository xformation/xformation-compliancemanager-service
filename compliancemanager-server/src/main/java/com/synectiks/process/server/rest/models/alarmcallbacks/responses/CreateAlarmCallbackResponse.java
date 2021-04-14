/*
 * */
package com.synectiks.process.server.rest.models.alarmcallbacks.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class CreateAlarmCallbackResponse {
    @JsonProperty("alarmcallback_id")
    public abstract String alarmCallbackId();

    @JsonCreator
    public static CreateAlarmCallbackResponse create(@JsonProperty("alarmcallback_id") String alarmCallbackId) {
        return new AutoValue_CreateAlarmCallbackResponse(alarmCallbackId);
    }
}
