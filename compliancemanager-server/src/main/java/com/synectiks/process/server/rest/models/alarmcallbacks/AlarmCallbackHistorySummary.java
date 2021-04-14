/*
 * */
package com.synectiks.process.server.rest.models.alarmcallbacks;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;
import org.joda.time.DateTime;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class AlarmCallbackHistorySummary {
    private static final String FIELD_ID = "id";
    private static final String FIELD_ALARMCALLBACKCONFIGURATION = "alarmcallbackconfiguration";
    private static final String FIELD_ALERT_ID = "alert_id";
    private static final String FIELD_ALERTCONDITION_ID = "alertcondition_id";
    private static final String FIELD_RESULT = "result";
    private static final String FIELD_CREATED_AT = "created_at";

    @JsonProperty(FIELD_ID)
    public abstract String id();

    @JsonProperty(FIELD_ALARMCALLBACKCONFIGURATION)
    public abstract AlarmCallbackSummary alarmcallbackConfiguration();

    @JsonProperty(FIELD_ALERT_ID)
    public abstract String alertId();

    @JsonProperty(FIELD_ALERTCONDITION_ID)
    public abstract String alertConditionId();

    @JsonProperty(FIELD_RESULT)
    public abstract AlarmCallbackResult result();

    @JsonProperty(FIELD_CREATED_AT)
    public abstract DateTime createdAt();

    @JsonCreator
    public static AlarmCallbackHistorySummary create(@JsonProperty(FIELD_ID) String id,
                                                  @JsonProperty(FIELD_ALARMCALLBACKCONFIGURATION) AlarmCallbackSummary alarmcallbackConfiguration,
                                                  @JsonProperty(FIELD_ALERT_ID) String alertId,
                                                  @JsonProperty(FIELD_ALERTCONDITION_ID) String alertConditionId,
                                                  @JsonProperty(FIELD_RESULT) AlarmCallbackResult result,
                                                  @JsonProperty(FIELD_CREATED_AT) DateTime createdAt) {
        return new AutoValue_AlarmCallbackHistorySummary(id, alarmcallbackConfiguration, alertId, alertConditionId, result, createdAt);
    }
}
