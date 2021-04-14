/*
 * */
package com.synectiks.process.server.rest.models.alarmcallbacks;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class AlarmCallbackSuccess extends AlarmCallbackResult {
    @JsonProperty("type")
    @Override
    public String type() { return "success"; }

    @JsonCreator
    public static AlarmCallbackSuccess create() {
        return new AutoValue_AlarmCallbackSuccess();
    }
}
