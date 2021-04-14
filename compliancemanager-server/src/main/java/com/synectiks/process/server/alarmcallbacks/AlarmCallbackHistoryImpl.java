/*
 * */
package com.synectiks.process.server.alarmcallbacks;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.alerts.Alert;
import com.synectiks.process.server.database.CollectionName;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.rest.models.alarmcallbacks.AlarmCallbackResult;
import com.synectiks.process.server.rest.models.alarmcallbacks.AlarmCallbackSummary;

import org.graylog.autovalue.WithBeanGetter;
import org.joda.time.DateTime;
import org.mongojack.Id;
import org.mongojack.ObjectId;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
@CollectionName("alarmcallbackhistory")
public abstract class AlarmCallbackHistoryImpl implements AlarmCallbackHistory {
    static final String FIELD_ID = "id";
    static final String FIELD_ALARMCALLBACKCONFIGURATION = "alarmcallbackconfiguration";
    static final String FIELD_ALERTID = "alert_id";
    static final String FIELD_ALERTCONDITIONID = "alertcondition_id";
    static final String FIELD_RESULT = "result";
    static final String FIELD_CREATED_AT = "created_at";

    @JsonProperty(FIELD_ID)
    @Id
    @ObjectId
    @Override
    public abstract String id();

    @JsonProperty(FIELD_ALARMCALLBACKCONFIGURATION)
    @Override
    public abstract AlarmCallbackSummary alarmcallbackConfiguration();

    @JsonProperty(FIELD_ALERTID)
    @Override
    public abstract String alertId();

    @JsonProperty(FIELD_ALERTCONDITIONID)
    @Override
    public abstract String alertConditionId();

    @JsonProperty(FIELD_RESULT)
    @Override
    public abstract AlarmCallbackResult result();

    @JsonProperty(FIELD_CREATED_AT)
    @Override
    public abstract DateTime createdAt();

    @JsonCreator
    public static AlarmCallbackHistoryImpl create(@JsonProperty(FIELD_ID) @Id @ObjectId String id,
                                              @JsonProperty(FIELD_ALARMCALLBACKCONFIGURATION) AlarmCallbackSummary alarmcallbackConfiguration,
                                              @JsonProperty(FIELD_ALERTID) String alertId,
                                              @JsonProperty(FIELD_ALERTCONDITIONID) String alertConditionId,
                                              @JsonProperty(FIELD_RESULT) AlarmCallbackResult result,
                                              @JsonProperty(FIELD_CREATED_AT) DateTime createdAt) {
        return new AutoValue_AlarmCallbackHistoryImpl(id, alarmcallbackConfiguration, alertId, alertConditionId, result, createdAt);
    }

    public static AlarmCallbackHistory create(String id,
                                              AlarmCallbackConfiguration alarmCallbackConfiguration,
                                              Alert alert,
                                              AlertCondition alertCondition,
                                              AlarmCallbackResult result,
                                              DateTime createdAt) {
        final AlarmCallbackSummary alarmCallbackSummary = AlarmCallbackSummary.create(
                alarmCallbackConfiguration.getId(),
                alarmCallbackConfiguration.getStreamId(),
                alarmCallbackConfiguration.getType(),
                alarmCallbackConfiguration.getTitle(),
                alarmCallbackConfiguration.getConfiguration(),
                alarmCallbackConfiguration.getCreatedAt(),
                alarmCallbackConfiguration.getCreatorUserId()
        );
        return create(id, alarmCallbackSummary, alert.getId(), alertCondition.getId(), result, createdAt);
    }

    public static AlarmCallbackHistory create(String id,
                                              AlarmCallbackConfiguration alarmCallbackConfiguration,
                                              Alert alert,
                                              AlertCondition alertCondition,
                                              AlarmCallbackResult result) {
        return create(id, alarmCallbackConfiguration, alert, alertCondition, result, Tools.nowUTC());
    }
}
