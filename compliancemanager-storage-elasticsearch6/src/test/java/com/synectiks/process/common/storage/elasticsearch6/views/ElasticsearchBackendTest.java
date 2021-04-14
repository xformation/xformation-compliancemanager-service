/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.views;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.QueryResult;
import com.synectiks.process.common.plugins.views.search.Search;
import com.synectiks.process.common.plugins.views.search.SearchJob;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.common.plugins.views.search.elasticsearch.ElasticsearchQueryString;
import com.synectiks.process.common.plugins.views.search.elasticsearch.FieldTypesLookup;
import com.synectiks.process.common.plugins.views.search.elasticsearch.IndexLookup;
import com.synectiks.process.common.plugins.views.search.elasticsearch.QueryStringDecorators;
import com.synectiks.process.common.plugins.views.search.elasticsearch.QueryStringParser;
import com.synectiks.process.common.plugins.views.search.searchtypes.MessageList;
import com.synectiks.process.common.storage.elasticsearch6.views.ESGeneratedQueryContext;
import com.synectiks.process.common.storage.elasticsearch6.views.ElasticsearchBackend;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.ESMessageList;
import com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.ESSearchTypeHandler;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.RelativeRange;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.inject.Provider;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ElasticsearchBackendTest {

    private static ElasticsearchBackend backend;

    @BeforeClass
    public static void setup() {
        Map<String, Provider<ESSearchTypeHandler<? extends SearchType>>> handlers = Maps.newHashMap();
        handlers.put(MessageList.NAME, () -> new ESMessageList(new QueryStringDecorators.Fake()));

        final FieldTypesLookup fieldTypesLookup = mock(FieldTypesLookup.class);
        final QueryStringParser queryStringParser = new QueryStringParser();
        backend = new ElasticsearchBackend(handlers,
                null,
                mock(IndexLookup.class),
                new QueryStringDecorators.Fake(),
                (elasticsearchBackend, ssb, job, query, results) -> new ESGeneratedQueryContext(elasticsearchBackend, ssb, job, query, results, fieldTypesLookup),
                false);
    }

    @Test
    public void generatesSearchForEmptySearchTypes() throws Exception {
        final Query query = Query.builder()
                .id("query1")
                .query(ElasticsearchQueryString.builder().queryString("").build())
                .timerange(RelativeRange.create(300))
                .build();
        final Search search = Search.builder().queries(ImmutableSet.of(query)).build();
        final SearchJob job = new SearchJob("deadbeef", search, "admin");

        backend.generate(job, query, Collections.emptySet());
    }

    @Test
    public void executesSearchForEmptySearchTypes() throws Exception {
        final Query query = Query.builder()
                .id("query1")
                .query(ElasticsearchQueryString.builder().queryString("").build())
                .timerange(RelativeRange.create(300))
                .build();
        final Search search = Search.builder().queries(ImmutableSet.of(query)).build();
        final SearchJob job = new SearchJob("deadbeef", search, "admin");

        final ESGeneratedQueryContext queryContext = mock(ESGeneratedQueryContext.class);

        final QueryResult queryResult = backend.doRun(job, query, queryContext, Collections.emptySet());

        assertThat(queryResult).isNotNull();
        assertThat(queryResult.searchTypes()).isEmpty();
        assertThat(queryResult.executionStats()).isNotNull();
        assertThat(queryResult.errors()).isEmpty();
    }
}
