/*
 * */
package com.synectiks.process.server.rest.models.system.contentpacks.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect

@AutoValue
@WithBeanGetter
public abstract class ContentPackMetadata {
    @JsonProperty
    public abstract int installationCount();

    @JsonCreator
    public static ContentPackMetadata create(@JsonProperty("installation_count") int installationCount) {
        return new AutoValue_ContentPackMetadata(installationCount);
    }
}
