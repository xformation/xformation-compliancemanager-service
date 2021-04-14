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
public abstract class RootResponse {
    public abstract VersionResponse version();

    @JsonCreator
    public static RootResponse create(@JsonProperty("version") VersionResponse versionResponse) {
        return new AutoValue_RootResponse(versionResponse);
    }
}
