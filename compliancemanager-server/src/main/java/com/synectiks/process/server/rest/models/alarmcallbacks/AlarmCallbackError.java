/*
 * */
package com.synectiks.process.server.rest.models.alarmcallbacks;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.base.Strings;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class AlarmCallbackError extends AlarmCallbackResult {
    @JsonProperty("type")
    @Override
    public String type() { return "error"; }

    @JsonProperty("error")
    public abstract String error();

    @JsonCreator
    public static AlarmCallbackError create(@JsonProperty("error") String error) {
        return new AutoValue_AlarmCallbackError(Strings.nullToEmpty(error));
    }
}
