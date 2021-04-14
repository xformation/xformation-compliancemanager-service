/*
 * */
package com.synectiks.process.server.cluster;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class UserPermissionMigrationState {

    @JsonProperty
    public abstract boolean migrationDone();

    @JsonCreator
    public static UserPermissionMigrationState create(@JsonProperty("migration_done") boolean migrationDone) {
        return new AutoValue_UserPermissionMigrationState(migrationDone);
    }
}
