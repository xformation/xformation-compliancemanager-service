/*
 * */
package com.synectiks.process.server.contentpacks.model.constraints;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect

@AutoValue
@WithBeanGetter
public abstract class ConstraintCheckResult {
    @JsonProperty
    public abstract Constraint constraint();

    @JsonProperty
    public abstract boolean fulfilled();

    @JsonCreator
    public static ConstraintCheckResult create(@JsonProperty("constraint") Constraint constraint, @JsonProperty("fulfilled") boolean fulfilled) {
       return new AutoValue_ConstraintCheckResult(constraint, fulfilled);
    }
}
