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
public abstract class AlertListSummary {
    @JsonProperty("total")
    public abstract long total();

    @JsonProperty("alerts")
    public abstract List<AlertSummary> alerts();

    @JsonCreator
    public static AlertListSummary create(@JsonProperty("total") long total,
                                          @JsonProperty("alerts") List<AlertSummary> alerts) {
        return new AutoValue_AlertListSummary(total, alerts);
    }
}
