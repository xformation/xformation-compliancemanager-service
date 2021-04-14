/*
 * */
package com.synectiks.process.server.rest.models.system.contentpacks.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.model.ContentPack;
import com.synectiks.process.server.contentpacks.model.ModelId;

import org.graylog.autovalue.WithBeanGetter;

import java.util.Map;
import java.util.Set;

@JsonAutoDetect

@AutoValue
@WithBeanGetter
public abstract class ContentPackList {
    @JsonProperty
    public abstract long total();

    @JsonProperty
    public abstract Set<ContentPack> contentPacks();

    @JsonProperty
    public abstract Map<ModelId, Map<Integer, ContentPackMetadata>> contentPacksMetadata();

    @JsonCreator
    public static ContentPackList create(@JsonProperty("total") long total,
                                         @JsonProperty("content_packs")Set<ContentPack> contentPacks,
                                         @JsonProperty("content_pack_metadata") Map<ModelId, Map<Integer, ContentPackMetadata>> contentPacksMetadata) {
        return new AutoValue_ContentPackList(total, contentPacks, contentPacksMetadata);
    }
}
