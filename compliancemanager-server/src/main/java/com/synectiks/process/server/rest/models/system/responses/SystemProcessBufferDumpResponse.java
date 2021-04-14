/*
 * */
package com.synectiks.process.server.rest.models.system.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class SystemProcessBufferDumpResponse {
    @JsonProperty("processbuffer_dump")
    public abstract Map<String, String> processBufferDump();

    @JsonCreator
    public static SystemProcessBufferDumpResponse create(@JsonProperty("processbuffer_dump") Map<String, String> processBufferDump) {
        return new AutoValue_SystemProcessBufferDumpResponse(processBufferDump);
    }
}
