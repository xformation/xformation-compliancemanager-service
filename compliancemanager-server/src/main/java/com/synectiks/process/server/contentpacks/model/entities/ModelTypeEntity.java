/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import org.apache.commons.lang.StringUtils;

@AutoValue
public abstract class ModelTypeEntity {
    @JsonValue
    public abstract ValueReference type();

    @Override
    public String toString() {
        return type().asString();
    }

    @JsonCreator
    public static ModelTypeEntity of(ValueReference type) {
        Preconditions.checkArgument(StringUtils.isNotBlank(type.asString()), "Type must not be blank");
        return new AutoValue_ModelTypeEntity(type);
    }

    public static ModelTypeEntity of(String type) {
        return of(ValueReference.of(type));
    }
}
