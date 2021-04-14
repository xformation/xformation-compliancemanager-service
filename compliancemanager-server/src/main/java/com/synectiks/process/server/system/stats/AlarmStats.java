/*
 * */
package com.synectiks.process.server.system.stats;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class AlarmStats {

    @JsonProperty
    public abstract long alertCount();

    @JsonProperty
    public abstract Map<String, Long> alarmcallbackCountByType();

    public static AlarmStats create(long alertCount, Map<String, Long> alarmcallbackCountByType) {
        return new AutoValue_AlarmStats(alertCount, alarmcallbackCountByType);
    }
}
