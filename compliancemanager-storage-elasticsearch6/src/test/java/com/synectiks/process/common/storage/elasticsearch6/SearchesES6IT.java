/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synectiks.process.common.storage.elasticsearch6.MultiSearch;
import com.synectiks.process.common.storage.elasticsearch6.Scroll;
import com.synectiks.process.common.storage.elasticsearch6.ScrollResultES6;
import com.synectiks.process.common.storage.elasticsearch6.SearchesAdapterES6;
import com.synectiks.process.common.storage.elasticsearch6.SortOrderMapper;
import com.synectiks.process.common.storage.elasticsearch6.testing.ElasticsearchInstanceES6;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;
import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.indexer.searches.Searches;
import com.synectiks.process.server.indexer.searches.SearchesAdapter;
import com.synectiks.process.server.indexer.searches.SearchesIT;

import static com.synectiks.process.common.storage.elasticsearch6.testing.TestUtils.jestClient;

import org.junit.Rule;

public class SearchesES6IT extends SearchesIT {
    @Rule
    public final ElasticsearchInstance elasticsearch = ElasticsearchInstanceES6.create();

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }

    private SearchesAdapter createSearchesAdapter() {
        final ScrollResultES6.Factory scrollResultFactory = (initialResult, query, scroll, fields, limit) -> new ScrollResultES6(
                jestClient(elasticsearch), new ObjectMapper(), initialResult, query, scroll, fields, limit
        );

        return new SearchesAdapterES6(
                new Configuration(),
                new MultiSearch(jestClient(elasticsearch)), new Scroll(scrollResultFactory, jestClient(elasticsearch)),
                new SortOrderMapper()
        );
    }

    @Override
    public Searches createSearches() {
        return new Searches(
                indexRangeService,
                metricRegistry,
                streamService,
                indices,
                indexSetRegistry,
                createSearchesAdapter()
        );
    }
}
