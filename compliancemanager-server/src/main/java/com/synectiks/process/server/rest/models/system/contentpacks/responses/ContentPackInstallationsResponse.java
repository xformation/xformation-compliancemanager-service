/*
 * */
package com.synectiks.process.server.rest.models.system.contentpacks.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.ContentPackInstallation;

import org.graylog.autovalue.WithBeanGetter;

import java.util.Set;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class ContentPackInstallationsResponse {
    @JsonProperty("total")
    public abstract long total();

    @JsonProperty("installations")
    public abstract Set<ContentPackInstallation> contentPackInstallations();

    @JsonCreator
    public static ContentPackInstallationsResponse create(@JsonProperty("total") long total, @JsonProperty("installations") Set<ContentPackInstallation> contentPackInstallations) {
        return new AutoValue_ContentPackInstallationsResponse(total, contentPackInstallations);
    }
}
