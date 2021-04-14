/*
 * */
package com.synectiks.process.server.rest.models.system.loggers.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.List;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class SingleSubsystemSummary {
    @JsonProperty
    public abstract String title();
    @JsonProperty
    public abstract List<String> categories();
    @JsonProperty
    public abstract String description();
    @JsonProperty
    public abstract String level();
    @JsonProperty("level_syslog")
    public abstract int levelSyslog();

    @JsonCreator
    public static SingleSubsystemSummary create(@JsonProperty("title") String title,
                                                @JsonProperty("categories") List<String> categories,
                                                @JsonProperty("description") String description,
                                                @JsonProperty("level") String level,
                                                @JsonProperty("level_syslog") int levelSyslog) {
        return new AutoValue_SingleSubsystemSummary(title, categories, description, level, levelSyslog);
    }
}
