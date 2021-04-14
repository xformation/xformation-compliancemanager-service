/*
 * */
package com.synectiks.process.server.rest.models.system.contentpacks.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.ContentPack;
import com.synectiks.process.server.contentpacks.model.constraints.ConstraintCheckResult;

import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;
import java.util.Set;

@JsonAutoDetect

@AutoValue
@WithBeanGetter
public abstract class ContentPackRevisions {
    @JsonProperty("content_pack_revisions")
    public abstract Map<Integer, ContentPack> contentPackRevisions();

    @JsonProperty("constraints_result")
    public abstract Map<Integer, Set<ConstraintCheckResult>> constraints();

    @JsonCreator
    public static ContentPackRevisions create(@JsonProperty("content_pack_revisions") Map<Integer, ContentPack> contentPackRevisions,
                                              @JsonProperty("constraints_result")Map<Integer, Set<ConstraintCheckResult>> constraints) {
        return new AutoValue_ContentPackRevisions(contentPackRevisions, constraints);
    }
}
