/*
 * */
package com.synectiks.process.server.rest.models.alarmcallbacks.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfiguration;

import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class CreateAlarmCallbackRequest {
    private static final String FIELD_TYPE = "type";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_CONFIGURATION = "configuration";

    @JsonProperty(FIELD_TYPE)
    public abstract String type();

    @JsonProperty(FIELD_TITLE)
    public abstract String title();

    @JsonProperty(FIELD_CONFIGURATION)
    public abstract Map<String, Object> configuration();

    @JsonCreator
    public static CreateAlarmCallbackRequest create(@JsonProperty(FIELD_TYPE) String type,
                                                    @JsonProperty(FIELD_TITLE) String title,
                                                    @JsonProperty(FIELD_CONFIGURATION) Map<String, Object> configuration) {
        return new AutoValue_CreateAlarmCallbackRequest(type, title, configuration);
    }

    public static CreateAlarmCallbackRequest create(AlarmCallbackConfiguration alarmCallbackConfiguration) {
        return create(alarmCallbackConfiguration.getType(), alarmCallbackConfiguration.getTitle(), alarmCallbackConfiguration.getConfiguration());
    }
}
