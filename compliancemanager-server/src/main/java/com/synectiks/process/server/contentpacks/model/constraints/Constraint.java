/*
 * */
package com.synectiks.process.server.contentpacks.model.constraints;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Optional;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = Constraint.FIELD_META_TYPE)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GraylogVersionConstraint.class, name = GraylogVersionConstraint.TYPE_NAME),
        @JsonSubTypes.Type(value = PluginVersionConstraint.class, name = PluginVersionConstraint.TYPE_NAME)
})
public interface Constraint {
    String FIELD_META_TYPE = "type";

    @JsonProperty(FIELD_META_TYPE)
    String type();

    interface ConstraintBuilder<SELF> {
        @JsonProperty(FIELD_META_TYPE)
        SELF type(String type);
    }
}
