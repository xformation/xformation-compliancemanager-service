/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.Map;
import java.util.Set;

@AutoValue
@JsonAutoDetect
public abstract class MigrationCompleted {
    private static final String FIELD_MIGRATED_DASHBOARD_IDS = "migrated_dashboard_ids";
    private static final String FIELD_WIDGET_MIGRATION_IDS = "widget_migration_ids";

    @JsonProperty(FIELD_MIGRATED_DASHBOARD_IDS)
    public abstract Set<String> migratedDashboardIds();

    @JsonProperty(FIELD_WIDGET_MIGRATION_IDS)
    public abstract Map<String, Set<String>> widgetMigrationIds();

    @JsonCreator
    static MigrationCompleted create(
            @JsonProperty(FIELD_MIGRATED_DASHBOARD_IDS) Set<String> migratedDashboardIds,
            @JsonProperty(FIELD_WIDGET_MIGRATION_IDS) Map<String, Set<String>> widgetMigrationIds
    ) {
        return new AutoValue_MigrationCompleted(migratedDashboardIds, widgetMigrationIds);
    }
}
