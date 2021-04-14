/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7.views.searchtypes.pivot.series;

import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.Pivot;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Average;
import com.synectiks.process.common.storage.elasticsearch7.views.ESGeneratedQueryContext;
import com.synectiks.process.common.storage.elasticsearch7.views.searchtypes.pivot.ESPivot;
import com.synectiks.process.common.storage.elasticsearch7.views.searchtypes.pivot.ESPivotSeriesSpecHandler;

import org.graylog.shaded.elasticsearch7.org.elasticsearch.action.search.SearchResponse;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.search.aggregations.AggregationBuilder;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.search.aggregations.AggregationBuilders;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.search.aggregations.metrics.Avg;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.stream.Stream;

public class ESAverageHandler extends ESPivotSeriesSpecHandler<Average, Avg> {
    @Nonnull
    @Override
    public Optional<AggregationBuilder> doCreateAggregation(String name, Pivot pivot, Average avgSpec, ESPivot searchTypeHandler, ESGeneratedQueryContext queryContext) {
        final AvgAggregationBuilder avg = AggregationBuilders.avg(name).field(avgSpec.field());
        record(queryContext, pivot, avgSpec, name, Avg.class);
        return Optional.of(avg);
    }

    @Override
    public Stream<ESPivotSeriesSpecHandler.Value> doHandleResult(Pivot pivot, Average pivotSpec,
                                                                 SearchResponse searchResult,
                                                                 Avg avgAggregation,
                                                                 ESPivot searchTypeHandler,
                                                                 ESGeneratedQueryContext esGeneratedQueryContext) {
        return Stream.of(ESPivotSeriesSpecHandler.Value.create(pivotSpec.id(), Average.NAME, avgAggregation.getValue()));
    }
}
