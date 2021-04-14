/*
 * */
package com.synectiks.process.server.contentpacks.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;

@AutoValue
public abstract class ModelVersion {
    @JsonValue
    public abstract String version();

    @Override
    public String toString() {
        return version();
    }

    @JsonCreator
    public static ModelVersion of(String version) {
        Preconditions.checkArgument(StringUtils.isNotBlank(version), "Version must not be blank");
        return new AutoValue_ModelVersion(version);
    }

}
