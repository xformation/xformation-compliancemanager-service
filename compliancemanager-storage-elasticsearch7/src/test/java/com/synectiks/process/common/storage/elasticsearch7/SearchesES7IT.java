/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7;

import com.synectiks.process.common.storage.elasticsearch7.Scroll;
import com.synectiks.process.common.storage.elasticsearch7.ScrollResultES7;
import com.synectiks.process.common.storage.elasticsearch7.SearchRequestFactory;
import com.synectiks.process.common.storage.elasticsearch7.SearchesAdapterES7;
import com.synectiks.process.common.storage.elasticsearch7.SortOrderMapper;
import com.synectiks.process.common.storage.elasticsearch7.testing.ElasticsearchInstanceES7;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchInstance;
import com.synectiks.process.server.indexer.searches.Searches;
import com.synectiks.process.server.indexer.searches.SearchesAdapter;
import com.synectiks.process.server.indexer.searches.SearchesIT;
import org.junit.Rule;

public class SearchesES7IT extends SearchesIT {
    @Rule
    public final ElasticsearchInstanceES7 elasticsearch = ElasticsearchInstanceES7.create();

    @Override
    protected ElasticsearchInstance elasticsearch() {
        return this.elasticsearch;
    }

    private SearchesAdapter createSearchesAdapter() {
        final ScrollResultES7.Factory scrollResultFactory = (initialResult, query, scroll, fields, limit) -> new ScrollResultES7(
                elasticsearch.elasticsearchClient(), initialResult, query, scroll, fields, limit
        );
        final SortOrderMapper sortOrderMapper = new SortOrderMapper();
        final boolean allowHighlighting = true;
        final boolean allowLeadingWildcardSearches = true;

        final SearchRequestFactory searchRequestFactory = new SearchRequestFactory(sortOrderMapper, allowHighlighting, allowLeadingWildcardSearches);
        return new SearchesAdapterES7(elasticsearch.elasticsearchClient(),
                new Scroll(elasticsearch.elasticsearchClient(),
                        scrollResultFactory,
                        searchRequestFactory),
                searchRequestFactory);
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
