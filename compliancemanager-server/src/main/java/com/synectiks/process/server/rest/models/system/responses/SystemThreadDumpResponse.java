/*
 * */
package com.synectiks.process.server.rest.models.system.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class SystemThreadDumpResponse {
    @JsonProperty("threaddump")
    public abstract String threadDump();

    @JsonCreator
    public static SystemThreadDumpResponse create(@JsonProperty("threaddump") String threadDump) {
        return new AutoValue_SystemThreadDumpResponse(threadDump);
    }
}
