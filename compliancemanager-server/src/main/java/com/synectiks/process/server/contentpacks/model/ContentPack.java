/*
 * */
package com.synectiks.process.server.contentpacks.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synectiks.process.server.contentpacks.model.constraints.Constraint;

import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = Versioned.FIELD_META_VERSION, defaultImpl = LegacyContentPack.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LegacyContentPack.class),
        @JsonSubTypes.Type(value = ContentPackV1.class, name = ContentPackV1.VERSION)
})
public interface ContentPack extends Identified, Revisioned, Versioned {
    interface ContentPackBuilder<SELF> extends IdBuilder<SELF>, RevisionBuilder<SELF>, VersionBuilder<SELF> {
    }
}
