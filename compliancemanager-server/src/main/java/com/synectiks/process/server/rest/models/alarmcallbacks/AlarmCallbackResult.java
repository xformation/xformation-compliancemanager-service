/*
 * */
package com.synectiks.process.server.rest.models.alarmcallbacks;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonAutoDetect
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = AlarmCallbackError.class, name = "error"),
                @JsonSubTypes.Type(value = AlarmCallbackSuccess.class, name = "success") })
public abstract class AlarmCallbackResult {
    public abstract String type();
}
