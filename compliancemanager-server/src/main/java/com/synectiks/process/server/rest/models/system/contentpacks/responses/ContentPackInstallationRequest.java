/*
 * */
package com.synectiks.process.server.rest.models.system.contentpacks.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class ContentPackInstallationRequest {
    @JsonProperty("parameters")
    public abstract Map<String, ValueReference> parameters();

    @JsonProperty("comment")
    @Nullable
    public abstract String comment();

    @JsonCreator
    public static ContentPackInstallationRequest create(
            @JsonProperty("parameters") @Nullable Map<String, ValueReference> parameters,
            @JsonProperty("comment") @Nullable String comment) {
        final Map<String, ValueReference> parameterMap = parameters == null ? Collections.emptyMap() : parameters;
        return new AutoValue_ContentPackInstallationRequest(parameterMap, comment);
    }
}
