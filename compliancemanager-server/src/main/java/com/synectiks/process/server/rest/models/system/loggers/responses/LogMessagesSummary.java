/*
 * */
package com.synectiks.process.server.rest.models.system.loggers.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Collection;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class LogMessagesSummary {

    @JsonProperty
    public abstract Collection<InternalLogMessage> messages();

    @JsonCreator
    public static LogMessagesSummary create(@JsonProperty("messages") Collection<InternalLogMessage> messages) {
        return new AutoValue_LogMessagesSummary(messages);
    }

}
