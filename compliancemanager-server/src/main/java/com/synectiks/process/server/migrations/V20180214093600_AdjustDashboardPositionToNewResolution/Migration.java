/*
 * */
package com.synectiks.process.server.migrations.V20180214093600_AdjustDashboardPositionToNewResolution;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.database.ValidationException;

import org.graylog.autovalue.WithBeanGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Migration adjusting the position of dashboard widgets to the higher resolution of the
 * grid layout.
 */
public class Migration extends com.synectiks.process.server.migrations.Migration {

    private static final Logger LOG = LoggerFactory.getLogger(Migration.class);
    private final MigrationDashboardService dashboardService;
    private final ClusterConfigService clusterConfigService;

    @Inject
    public Migration(MigrationDashboardService dashboardService,
                     ClusterConfigService clusterConfigService) {
        this.dashboardService = dashboardService;
        this.clusterConfigService = clusterConfigService;
    }


    public ZonedDateTime createdAt() { return ZonedDateTime.parse("2018-02-14T09:36:00Z"); }

    @Override
    public void upgrade() {
        if (clusterConfigService.get(MigrationCompleted.class) != null) {
            LOG.debug("Migration already completed!");
            return;
        }

        Map<String, String> dashboardIds = new HashMap<>();
        for (MigrationDashboard dashboard : dashboardService.all()) {
            final List<WidgetPosition> oldPositions = dashboard.getPositions();
            if (oldPositions.isEmpty()) {
                dashboardIds.put(dashboard.getId(), "skipped");
                continue;
            }
            final List<WidgetPosition> widgetPositions = new ArrayList<>(oldPositions.size());

            for (WidgetPosition position : oldPositions) {
                int newWidth = position.width() * 2;
                int newHeight = position.height() * 2;
                int newCol = adjustPosition(position.col());
                int newRow = adjustPosition(position.row());
                widgetPositions.add(WidgetPosition.builder()
                        .id(position.id())
                        .width(newWidth)
                        .height(newHeight)
                        .col(newCol)
                        .row( newRow)
                        .build());
            }
            try {
                dashboard.setPositions(widgetPositions);
                dashboardService.save(dashboard);
                dashboardIds.put(dashboard.getId(), "updated");
            } catch (ValidationException e) {
                LOG.error("Could not update dashboard position: {}", e);
            }
        }
        clusterConfigService.write(MigrationCompleted.create(dashboardIds));
    }

    /* We double the resolution in space starting with 1.
       To keep widgets on the same position we need to subtract 1 from the result. */
    private int adjustPosition(int value) {
        return value * 2 - 1;
    }

    @JsonAutoDetect
    @AutoValue
    @WithBeanGetter
    public static abstract class MigrationCompleted {
        @JsonProperty("dashboard_ids")
        public abstract Map<String, String> dashboard_ids();

        @JsonCreator
        public static MigrationCompleted create(@JsonProperty("dashboard_ids")Map<String, String> dashboardIds) {
            return new AutoValue_Migration_MigrationCompleted(dashboardIds);
        }
    }
}
