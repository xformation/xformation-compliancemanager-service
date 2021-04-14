/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7.views;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.QueryResult;
import com.synectiks.process.common.plugins.views.search.Search;
import com.synectiks.process.common.plugins.views.search.SearchJob;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.common.plugins.views.search.elasticsearch.FieldTypesLookup;
import com.synectiks.process.common.plugins.views.search.elasticsearch.IndexLookup;
import com.synectiks.process.common.plugins.views.search.elasticsearch.QueryStringDecorators;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.BucketSpec;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.Pivot;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.SeriesSpec;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Average;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.series.Max;
import com.synectiks.process.common.storage.elasticsearch7.ElasticsearchClient;
import com.synectiks.process.common.storage.elasticsearch7.views.ESGeneratedQueryContext;
import com.synectiks.process.common.storage.elasticsearch7.views.ElasticsearchBackend;
import com.synectiks.process.common.storage.elasticsearch7.views.searchtypes.ESSearchTypeHandler;
import com.synectiks.process.common.storage.elasticsearch7.views.searchtypes.pivot.ESPivot;
import com.synectiks.process.common.storage.elasticsearch7.views.searchtypes.pivot.ESPivotBucketSpecHandler;
import com.synectiks.process.common.storage.elasticsearch7.views.searchtypes.pivot.ESPivotSeriesSpecHandler;
import com.synectiks.process.common.storage.elasticsearch7.views.searchtypes.pivot.series.ESAverageHandler;
import com.synectiks.process.common.storage.elasticsearch7.views.searchtypes.pivot.series.ESMaxHandler;

import org.graylog.shaded.elasticsearch7.org.elasticsearch.action.search.SearchRequest;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.search.aggregations.Aggregation;

import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.InvalidRangeParametersException;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.inject.Provider;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ElasticsearchBackendGeneratedRequestTestBase {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    ElasticsearchBackend elasticsearchBackend;

    @Mock
    protected ElasticsearchClient client;

    @Mock
    protected IndexLookup indexLookup;

    @Mock
    protected FieldTypesLookup fieldTypesLookup;

    protected Map<String, Provider<ESSearchTypeHandler<? extends SearchType>>> elasticSearchTypeHandlers;

    @Captor
    protected ArgumentCaptor<List<SearchRequest>> clientRequestCaptor;

    @Before
    public void setUpSUT() {
        this.elasticSearchTypeHandlers = new HashMap<>();
        final Map<String, ESPivotBucketSpecHandler<? extends BucketSpec, ? extends Aggregation>> bucketHandlers = Collections.emptyMap();
        final Map<String, ESPivotSeriesSpecHandler<? extends SeriesSpec, ? extends Aggregation>> seriesHandlers = new HashMap<>();
        seriesHandlers.put(Average.NAME, new ESAverageHandler());
        seriesHandlers.put(Max.NAME, new ESMaxHandler());
        elasticSearchTypeHandlers.put(Pivot.NAME, () -> new ESPivot(bucketHandlers, seriesHandlers));

        this.elasticsearchBackend = new ElasticsearchBackend(elasticSearchTypeHandlers,
                client,
                indexLookup,
                new QueryStringDecorators.Fake(),
                (elasticsearchBackend, ssb, job, query, results) -> new ESGeneratedQueryContext(elasticsearchBackend, ssb, job, query, results, fieldTypesLookup),
                false);
    }

    SearchJob searchJobForQuery(Query query) {
        final Search search = Search.builder()
                .id("search1")
                .queries(ImmutableSet.of(query))
                .build();
        return new SearchJob("job1", search, "admin");
    }

    TimeRange timeRangeForTest() {
        try {
            return AbsoluteRange.create("2018-08-23T10:02:00.247+02:00", "2018-08-23T10:07:00.252+02:00");
        } catch (InvalidRangeParametersException ignored) {
        }
        return null;
    }

    List<SearchRequest> run(SearchJob searchJob, Query query, ESGeneratedQueryContext queryContext, Set<QueryResult> predecessorResults) {
        this.elasticsearchBackend.doRun(searchJob, query, queryContext, predecessorResults);

        verify(client, times(1)).msearch(clientRequestCaptor.capture(), any());

        return clientRequestCaptor.getValue();
    }
}
