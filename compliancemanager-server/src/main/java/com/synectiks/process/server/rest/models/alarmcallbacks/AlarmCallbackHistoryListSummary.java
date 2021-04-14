/*
 * */
package com.synectiks.process.server.rest.models.alarmcallbacks;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class AlarmCallbackHistoryListSummary {
    private static final String FIELD_TOTAL = "total";
    private static final String FIELD_HISTORIES = "histories";

    @JsonProperty
    public abstract int total();

    @JsonProperty(FIELD_HISTORIES)
    public abstract List<AlarmCallbackHistorySummary> histories();

    @JsonCreator
    public static AlarmCallbackHistoryListSummary create(@JsonProperty(FIELD_TOTAL) int total,
                                                         @JsonProperty(FIELD_HISTORIES) List<AlarmCallbackHistorySummary> histories) {
        return new AutoValue_AlarmCallbackHistoryListSummary(total, histories);
    }

    public static AlarmCallbackHistoryListSummary create(List<AlarmCallbackHistorySummary> histories) {
        return new AutoValue_AlarmCallbackHistoryListSummary(histories.size(), histories);
    }
}
