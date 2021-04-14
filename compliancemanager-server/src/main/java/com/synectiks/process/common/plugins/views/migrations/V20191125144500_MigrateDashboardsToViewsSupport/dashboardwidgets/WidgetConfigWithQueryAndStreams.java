/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.dashboardwidgets;

import java.util.Collections;
import java.util.Optional;

import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.AggregationWidget;

public interface WidgetConfigWithQueryAndStreams extends WidgetConfigWithTimeRange {
    String query();

    Optional<String> streamId();

    default AggregationWidget.Builder createAggregationWidget(String id) {
        final AggregationWidget.Builder viewWidgetBuilder = AggregationWidget.builder()
                .id(id)
                .query(query())
                .timerange(timerange());
        return streamId().map(streamId -> viewWidgetBuilder.streams(Collections.singleton(streamId))).orElse(viewWidgetBuilder);
    }
}
