/*
 * */
package com.synectiks.process.common.plugins.views;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.synectiks.process.common.plugins.views.audit.ViewsAuditEventTypes;
import com.synectiks.process.common.plugins.views.migrations.V20181220133700_AddViewsAdminRole;
import com.synectiks.process.common.plugins.views.migrations.V20190127111728_MigrateWidgetFormatSettings;
import com.synectiks.process.common.plugins.views.migrations.V20190304102700_MigrateMessageListStructure;
import com.synectiks.process.common.plugins.views.migrations.V20190805115800_RemoveDashboardStateFromViews;
import com.synectiks.process.common.plugins.views.migrations.V20191204000000_RemoveLegacyViewsPermissions;
import com.synectiks.process.common.plugins.views.migrations.V20200409083200_RemoveRootQueriesFromMigratedDashboards;
import com.synectiks.process.common.plugins.views.migrations.V20200730000000_AddGl2MessageIdFieldAliasForEvents;
import com.synectiks.process.common.plugins.views.migrations.V20191125144500_MigrateDashboardsToViewsSupport.V20191125144500_MigrateDashboardsToViews;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.V20191203120602_MigrateSavedSearchesToViews;
import com.synectiks.process.common.plugins.views.migrations.V20200204122000_MigrateUntypedViewsToDashboards.V20200204122000_MigrateUntypedViewsToDashboards;
import com.synectiks.process.common.plugins.views.providers.ExportBackendProvider;
import com.synectiks.process.common.plugins.views.search.SearchRequirements;
import com.synectiks.process.common.plugins.views.search.SearchRequiresParameterSupport;
import com.synectiks.process.common.plugins.views.search.ValueParameter;
import com.synectiks.process.common.plugins.views.search.db.InMemorySearchJobService;
import com.synectiks.process.common.plugins.views.search.db.SearchJobService;
import com.synectiks.process.common.plugins.views.search.db.SearchesCleanUpJob;
import com.synectiks.process.common.plugins.views.search.elasticsearch.ElasticsearchQueryString;
import com.synectiks.process.common.plugins.views.search.export.ChunkDecorator;
import com.synectiks.process.common.plugins.views.search.export.DecoratingMessagesExporter;
import com.synectiks.process.common.plugins.views.search.export.ExportBackend;
import com.synectiks.process.common.plugins.views.search.export.LegacyChunkDecorator;
import com.synectiks.process.common.plugins.views.search.export.MessagesExporter;
import com.synectiks.process.common.plugins.views.search.export.SimpleMessageChunkCsvWriter;
import com.synectiks.process.common.plugins.views.search.filter.AndFilter;
import com.synectiks.process.common.plugins.views.search.filter.OrFilter;
import com.synectiks.process.common.plugins.views.search.filter.QueryStringFilter;
import com.synectiks.process.common.plugins.views.search.filter.StreamFilter;
import com.synectiks.process.common.plugins.views.search.rest.ViewsRestPermissions;
import com.synectiks.process.common.plugins.views.search.rest.exceptionmappers.MissingCapabilitiesExceptionMapper;
import com.synectiks.process.common.plugins.views.search.rest.exceptionmappers.PermissionExceptionMapper;
import com.synectiks.process.common.plugins.views.search.searchtypes.MessageList;
import com.synectiks.process.common.plugins.views.search.searchtypes.events.EventList;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.Pivot;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.PivotSort;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.SeriesSort;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets.AutoInterval;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets.Time;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets.TimeUnitInterval;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets.Values;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Average;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Cardinality;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Count;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Max;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Min;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Percentile;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.StdDev;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Sum;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.SumOfSquares;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Variance;
import com.synectiks.process.common.plugins.views.search.views.RequiresParameterSupport;
import com.synectiks.process.common.plugins.views.search.views.ViewRequirements;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.AggregationConfigDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.AreaVisualizationConfigDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.AutoIntervalDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.BarVisualizationConfigDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.HeatmapVisualizationConfigDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.LineVisualizationConfigDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.NumberVisualizationConfigDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.TimeHistogramConfigDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.TimeUnitIntervalDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.ValueConfigDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.WorldMapVisualizationConfigDTO;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.sort.PivotSortConfig;
import com.synectiks.process.common.plugins.views.search.views.widgets.aggregation.sort.SeriesSortConfig;
import com.synectiks.process.common.plugins.views.search.views.widgets.messagelist.MessageListConfigDTO;
import com.synectiks.process.server.plugin.PluginConfigBean;

import java.util.Set;

public class ViewsBindings extends ViewsModule {
    @Override
    public Set<? extends PluginConfigBean> getConfigBeans() {
        return ImmutableSet.of(new ViewsConfig());
    }

    @Override
    protected void configure() {
        registerExportBackendProvider();

        registerRestControllerPackage(getClass().getPackage().getName());

        addPermissions(ViewsRestPermissions.class);

        // Calling this once to set up binder, so injection does not fail.
        esQueryDecoratorBinder();

        // filter
        registerJacksonSubtype(AndFilter.class);
        registerJacksonSubtype(OrFilter.class);
        registerJacksonSubtype(StreamFilter.class);
        registerJacksonSubtype(QueryStringFilter.class);

        // query backends for jackson
        registerJacksonSubtype(ElasticsearchQueryString.class);

        // search types
        registerJacksonSubtype(MessageList.class);
        registerJacksonSubtype(Pivot.class);
        registerJacksonSubtype(EventList.class);

        // pivot specs
        registerJacksonSubtype(Values.class);
        registerJacksonSubtype(Time.class);
        registerPivotAggregationFunction(Average.NAME, Average.class);
        registerPivotAggregationFunction(Cardinality.NAME, Cardinality.class);
        registerPivotAggregationFunction(Count.NAME, Count.class);
        registerPivotAggregationFunction(Max.NAME, Max.class);
        registerPivotAggregationFunction(Min.NAME, Min.class);
        registerPivotAggregationFunction(StdDev.NAME, StdDev.class);
        registerPivotAggregationFunction(Sum.NAME, Sum.class);
        registerPivotAggregationFunction(SumOfSquares.NAME, SumOfSquares.class);
        registerPivotAggregationFunction(Variance.NAME, Variance.class);
        registerPivotAggregationFunction(Percentile.NAME, Percentile.class);

        registerJacksonSubtype(TimeUnitInterval.class);
        registerJacksonSubtype(TimeUnitIntervalDTO.class);
        registerJacksonSubtype(AutoInterval.class);
        registerJacksonSubtype(AutoIntervalDTO.class);

        bind(SearchJobService.class).to(InMemorySearchJobService.class).in(Scopes.SINGLETON);
        bind(ChunkDecorator.class).to(LegacyChunkDecorator.class);
        bind(MessagesExporter.class).to(DecoratingMessagesExporter.class);

        registerWidgetConfigSubtypes();

        registerVisualizationConfigSubtypes();

        addPeriodical(SearchesCleanUpJob.class);

        addMigration(V20181220133700_AddViewsAdminRole.class);
        addMigration(V20190304102700_MigrateMessageListStructure.class);
        addMigration(V20190805115800_RemoveDashboardStateFromViews.class);
        addMigration(V20191204000000_RemoveLegacyViewsPermissions.class);
        addMigration(V20191125144500_MigrateDashboardsToViews.class);
        addMigration(V20191203120602_MigrateSavedSearchesToViews.class);
        addMigration(V20190127111728_MigrateWidgetFormatSettings.class);
        addMigration(V20200204122000_MigrateUntypedViewsToDashboards.class);
        addMigration(V20200409083200_RemoveRootQueriesFromMigratedDashboards.class);
        addMigration(V20200730000000_AddGl2MessageIdFieldAliasForEvents.class);

        addAuditEventTypes(ViewsAuditEventTypes.class);

        registerSortConfigSubclasses();
        registerParameterSubtypes();

        install(new FactoryModuleBuilder().build(ViewRequirements.Factory.class));
        install(new FactoryModuleBuilder().build(SearchRequirements.Factory.class));

        registerViewRequirement(RequiresParameterSupport.class);
        registerSearchRequirement(SearchRequiresParameterSupport.class);

        // trigger capability binder once to set it up
        viewsCapabilityBinder();
        queryMetadataDecoratorBinder();

        registerExceptionMappers();

        jerseyAdditionalComponentsBinder().addBinding().toInstance(SimpleMessageChunkCsvWriter.class);
    }

    private void registerExportBackendProvider() {
        binder().bind(ExportBackend.class).toProvider(ExportBackendProvider.class);
    }

    private void registerSortConfigSubclasses() {
        registerJacksonSubtype(SeriesSortConfig.class);
        registerJacksonSubtype(PivotSortConfig.class);
        registerJacksonSubtype(SeriesSort.class);
        registerJacksonSubtype(PivotSort.class);
    }

    private void registerWidgetConfigSubtypes() {
        registerJacksonSubtype(AggregationConfigDTO.class);
        registerJacksonSubtype(MessageListConfigDTO.class);

        registerJacksonSubtype(TimeHistogramConfigDTO.class);
        registerJacksonSubtype(ValueConfigDTO.class);
    }

    private void registerVisualizationConfigSubtypes() {
        registerJacksonSubtype(WorldMapVisualizationConfigDTO.class);
        registerJacksonSubtype(BarVisualizationConfigDTO.class);
        registerJacksonSubtype(NumberVisualizationConfigDTO.class);
        registerJacksonSubtype(LineVisualizationConfigDTO.class);
        registerJacksonSubtype(AreaVisualizationConfigDTO.class);
        registerJacksonSubtype(HeatmapVisualizationConfigDTO.class);
    }

    private void registerParameterSubtypes() {
        registerJacksonSubtype(ValueParameter.class);
    }

    private void registerExceptionMappers() {
        addJerseyExceptionMapper(MissingCapabilitiesExceptionMapper.class);
        addJerseyExceptionMapper(PermissionExceptionMapper.class);
    }
}
