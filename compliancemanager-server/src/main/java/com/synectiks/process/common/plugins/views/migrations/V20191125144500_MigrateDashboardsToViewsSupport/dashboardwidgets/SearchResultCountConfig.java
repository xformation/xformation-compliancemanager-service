/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.dashboardwidgets;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.base.Strings;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.RandomUUIDProvider;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.TimeRange;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.ViewWidget;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.Widget;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.AggregationConfig;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.NumberVisualizationConfig;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.viewwidgets.Series;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@AutoValue
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SearchResultCountConfig extends WidgetConfigBase implements WidgetConfigWithQueryAndStreams {
    private static final String NUMERIC_VISUALIZATION = "numeric";

    public abstract Optional<Boolean> lowerIsBetter();

    public abstract Optional<Boolean> trend();

    public abstract Optional<String> streamId();

    private Series series() {
        return countSeries();
    }

    private String visualization() {
        return NUMERIC_VISUALIZATION;
    }

    @Override
    public Set<ViewWidget> toViewWidgets(Widget widget, RandomUUIDProvider randomUUIDProvider) {
        return Collections.singleton(
                createAggregationWidget(randomUUIDProvider.get())
                        .config(
                                AggregationConfig.builder()
                                        .series(Collections.singletonList(series()))
                                        .visualization(visualization())
                                        .visualizationConfig(
                                                NumberVisualizationConfig.builder()
                                                        .trend(true)
                                                        .trendPreference(lowerIsBetter().orElse(false)
                                                                ? NumberVisualizationConfig.TrendPreference.LOWER
                                                                : NumberVisualizationConfig.TrendPreference.HIGHER)
                                                        .build()
                                        )
                                        .build()
                        ).build()
        );
    }

    @JsonCreator
    static SearchResultCountConfig create(
            @JsonProperty("lower_is_better") @Nullable Boolean lowerIsBetter,
            @JsonProperty("trend") @Nullable Boolean trend,
            @JsonProperty("query") @Nullable String query,
            @JsonProperty("timerange") TimeRange timerange,
            @JsonProperty("stream_id") @Nullable String streamId
    ) {
        return new AutoValue_SearchResultCountConfig(
                timerange,
                Strings.nullToEmpty(query),
                Optional.ofNullable(lowerIsBetter),
                Optional.ofNullable(trend),
                Optional.ofNullable(streamId)
        );
    }
}
