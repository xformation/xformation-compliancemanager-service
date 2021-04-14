/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.multibindings.MapBinder;
import com.synectiks.process.common.plugins.views.ViewsModule;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.common.plugins.views.search.elasticsearch.ElasticsearchQueryString;
import com.synectiks.process.common.plugins.views.search.engine.GeneratedQueryContext;
import com.synectiks.process.common.plugins.views.search.engine.QueryBackend;
import com.synectiks.process.common.plugins.views.search.export.ExportBackend;
import com.synectiks.process.common.plugins.views.search.searchtypes.MessageList;
import com.synectiks.process.common.plugins.views.search.searchtypes.events.EventList;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.BucketSpec;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.Pivot;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.SeriesSpec;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets.DateRangeBucket;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.buckets.Time;
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
import com.synectiks.process.common.storage.elasticsearch6.views.ESGeneratedQueryContext;
import com.synectiks.process.common.storage.elasticsearch6.views.ElasticsearchBackend;
import com.synectiks.process.common.storage.elasticsearch6.views.export.ElasticsearchExportBackend;
import com.synectiks.process.common.storage.elasticsearch6.views.export.RequestStrategy;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.ESEventList;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.ESMessageList;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.ESSearchTypeHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.ESPivot;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.ESPivotBucketSpecHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.ESPivotSeriesSpecHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.buckets.ESDateRangeHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.buckets.ESTimeHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.buckets.ESValuesHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.series.ESAverageHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.series.ESCardinalityHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.series.ESCountHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.series.ESMaxHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.series.ESMinHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.series.ESPercentilesHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.series.ESStdDevHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.series.ESSumHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.series.ESSumOfSquaresHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot.series.ESVarianceHandler;

import io.searchbox.core.search.aggregation.Aggregation;

import static com.synectiks.process.common.storage.elasticsearch6.Elasticsearch6Plugin.SUPPORTED_ES_VERSION;

public class ViewsESBackendModule extends ViewsModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().build(ESGeneratedQueryContext.Factory.class));

        bindForVersion(SUPPORTED_ES_VERSION, new TypeLiteral<QueryBackend<? extends GeneratedQueryContext>>() {})
            .to(ElasticsearchBackend.class);

        registerESSearchTypeHandler(MessageList.NAME, ESMessageList.class);
        registerESSearchTypeHandler(EventList.NAME, ESEventList.class);
        registerESSearchTypeHandler(Pivot.NAME, ESPivot.class).in(Scopes.SINGLETON);

        registerPivotSeriesHandler(Average.NAME, ESAverageHandler.class);
        registerPivotSeriesHandler(Cardinality.NAME, ESCardinalityHandler.class);
        registerPivotSeriesHandler(Count.NAME, ESCountHandler.class);
        registerPivotSeriesHandler(Max.NAME, ESMaxHandler.class);
        registerPivotSeriesHandler(Min.NAME, ESMinHandler.class);
        registerPivotSeriesHandler(StdDev.NAME, ESStdDevHandler.class);
        registerPivotSeriesHandler(Sum.NAME, ESSumHandler.class);
        registerPivotSeriesHandler(SumOfSquares.NAME, ESSumOfSquaresHandler.class);
        registerPivotSeriesHandler(Variance.NAME, ESVarianceHandler.class);
        registerPivotSeriesHandler(Percentile.NAME, ESPercentilesHandler.class);

        registerPivotBucketHandler(Values.NAME, ESValuesHandler.class);
        registerPivotBucketHandler(Time.NAME, ESTimeHandler.class);
        registerPivotBucketHandler(DateRangeBucket.NAME, ESDateRangeHandler.class);

        bindExportBackend().to(ElasticsearchExportBackend.class);
        bindRequestStrategy().to(com.synectiks.process.common.storage.elasticsearch6.views.export.Scroll.class);
    }

    private LinkedBindingBuilder<RequestStrategy> bindRequestStrategy() {
        return bind(RequestStrategy.class);
    }

    private LinkedBindingBuilder<ExportBackend> bindExportBackend() {
        return bindExportBackend(SUPPORTED_ES_VERSION);
    }

    private void registerQueryBackend() {
        registerQueryBackend(SUPPORTED_ES_VERSION, ElasticsearchQueryString.NAME, ElasticsearchBackend.class);
    }

    private MapBinder<String, ESPivotBucketSpecHandler<? extends BucketSpec, ? extends Aggregation>> pivotBucketHandlerBinder() {
        return MapBinder.newMapBinder(binder(),
                TypeLiteral.get(String.class),
                new TypeLiteral<ESPivotBucketSpecHandler<? extends BucketSpec, ? extends Aggregation>>() {});

    }

    private ScopedBindingBuilder registerPivotBucketHandler(
            String name,
            Class<? extends ESPivotBucketSpecHandler<? extends BucketSpec, ? extends Aggregation>> implementation
    ) {
        return pivotBucketHandlerBinder().addBinding(name).to(implementation);
    }

    protected MapBinder<String, ESPivotSeriesSpecHandler<? extends SeriesSpec, ? extends Aggregation>> pivotSeriesHandlerBinder() {
        return MapBinder.newMapBinder(binder(),
                TypeLiteral.get(String.class),
                new TypeLiteral<ESPivotSeriesSpecHandler<? extends SeriesSpec, ? extends Aggregation>>() {});

    }

    private ScopedBindingBuilder registerPivotSeriesHandler(
            String name,
            Class<? extends ESPivotSeriesSpecHandler<? extends SeriesSpec, ? extends Aggregation>> implementation
    ) {
        return pivotSeriesHandlerBinder().addBinding(name).to(implementation);
    }

    private MapBinder<String, ESSearchTypeHandler<? extends SearchType>> esSearchTypeHandlerBinder() {
        return MapBinder.newMapBinder(binder(),
                TypeLiteral.get(String.class),
                new TypeLiteral<ESSearchTypeHandler<? extends SearchType>>() {});
    }

    private ScopedBindingBuilder registerESSearchTypeHandler(String name, Class<? extends ESSearchTypeHandler<? extends SearchType>> implementation) {
        return esSearchTypeHandlerBinder().addBinding(name).to(implementation);
    }
}
