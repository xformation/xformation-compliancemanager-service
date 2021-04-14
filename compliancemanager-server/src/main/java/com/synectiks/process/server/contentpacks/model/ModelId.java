/*
 * */
package com.synectiks.process.server.contentpacks.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;


@AutoValue
public abstract class ModelId {
    @JsonValue
    public abstract String id();

    @Override
    public String toString() {
        return id();
    }

    @JsonCreator
    public static ModelId of(String id) {
        Preconditions.checkArgument(StringUtils.isNotBlank(id), "ID must not be blank");
        return new AutoValue_ModelId(id);
    }
}
