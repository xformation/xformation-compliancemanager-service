/*
 * */
package com.synectiks.process.server.contentpacks.model.constraints;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.semver4j.Requirement;

@AutoValue
@JsonDeserialize(builder = AutoValue_GraylogVersionConstraint.Builder.class)
public abstract class GraylogVersionConstraint implements Constraint {
    public static final String TYPE_NAME = "server-version";
    private static final String FIELD_VERSION = "version";

    @JsonProperty(FIELD_VERSION)
    public abstract Requirement version();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_GraylogVersionConstraint.Builder();
    }

    public static GraylogVersionConstraint of(Version version) {
        final String versionString = version.toString().replace("-SNAPSHOT", "");
        final Requirement requirement = Requirement.buildNPM(">=" + versionString);
        return builder()
                .version(requirement)
                .build();
    }

    public static GraylogVersionConstraint currentGraylogVersion() {
        return of(Version.CURRENT_CLASSPATH);
    }

    @AutoValue.Builder
    public abstract static class Builder implements Constraint.ConstraintBuilder<Builder> {
        @JsonProperty(FIELD_VERSION)
        public abstract Builder version(Requirement version);

        @JsonIgnore
        public Builder version(String versionExpression) {
            final Requirement requirement = Requirement.buildNPM(versionExpression);
            return version(requirement);
        }

        abstract GraylogVersionConstraint autoBuild();

        public GraylogVersionConstraint build() {
            type(TYPE_NAME);
            return autoBuild();
        }
    }
}
