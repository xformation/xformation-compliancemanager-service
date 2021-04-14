/*
 * */
package com.synectiks.process.server.storage.versionprobe;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class VersionResponse {
    public abstract String number();

    @JsonCreator
    public static VersionResponse create(@JsonProperty("number") String number) {
        return new AutoValue_VersionResponse(number);
    }
}
