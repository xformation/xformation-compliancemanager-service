/*
 * */
package com.synectiks.process.server.rest.models.streams.alerts;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class AlertConditionListSummary {
    @JsonProperty("total")
    public abstract long total();

    @JsonProperty("conditions")
    public abstract List<AlertConditionSummary> conditions();

    @JsonCreator
    public static AlertConditionListSummary create(@JsonProperty("total") long total,
                                                   @JsonProperty("conditions") List<AlertConditionSummary> conditions) {
        return new AutoValue_AlertConditionListSummary(total, conditions);
    }

    public static AlertConditionListSummary create(@JsonProperty("conditions") List<AlertConditionSummary> conditions) {
        return new AutoValue_AlertConditionListSummary(conditions.size(), conditions);
    }
}
