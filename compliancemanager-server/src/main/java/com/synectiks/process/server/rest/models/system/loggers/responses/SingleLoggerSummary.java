/*
 * */
package com.synectiks.process.server.rest.models.system.loggers.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class SingleLoggerSummary {
    @JsonProperty
    public abstract String level();
    @JsonProperty("level_syslog")
    public abstract int levelSyslog();

    @JsonCreator
    public static SingleLoggerSummary create(@JsonProperty("level") String level, @JsonProperty("level_syslog") int levelSyslog) {
        return new AutoValue_SingleLoggerSummary(level, levelSyslog);
    }
}
