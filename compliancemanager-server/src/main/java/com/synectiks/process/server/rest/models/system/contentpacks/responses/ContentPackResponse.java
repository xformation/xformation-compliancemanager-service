/*
 * */
package com.synectiks.process.server.rest.models.system.contentpacks.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.ContentPack;
import com.synectiks.process.server.contentpacks.model.constraints.ConstraintCheckResult;

import java.util.Set;

@JsonAutoDetect
@AutoValue
public abstract class ContentPackResponse {
    @JsonProperty("content_pack")
    public abstract ContentPack contentPack();

    @JsonProperty("constraints_result")
    public abstract Set<ConstraintCheckResult> constraints();

    @JsonCreator
    public static ContentPackResponse create(@JsonProperty("content_pack") ContentPack contentPack, @JsonProperty("constraints_result") Set<ConstraintCheckResult> constraints) {
        return new AutoValue_ContentPackResponse(contentPack, constraints);
    }
}
