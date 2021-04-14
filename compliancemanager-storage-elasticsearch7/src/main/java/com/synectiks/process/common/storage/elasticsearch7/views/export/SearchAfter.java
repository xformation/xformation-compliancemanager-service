/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7.views.export;

import com.google.common.collect.Streams;
import com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.action.search.SearchRequest;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.action.search.SearchResponse;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.search.SearchHit;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.search.builder.SearchSourceBuilder;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.search.sort.SortOrder;
import com.synectiks.process.server.plugin.Message;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class SearchAfter implements RequestStrategy {

    static final String DEFAULT_TIEBREAKER_FIELD = Message.FIELD_XFALERT_MESSAGE_ID;
    static final String EVENTS_TIEBREAKER_FIELD = Message.FIELD_ID;

    private final ExportClient client;

    private Object[] searchAfterValues = null;

    @Inject
    public SearchAfter(ExportClient client) {
        this.client = client;
    }

    @Override
    public List<SearchHit> nextChunk(SearchRequest search, ExportMessagesCommand command) {

        SearchResponse result = search(search);
        List<SearchHit> hits = Streams.stream(result.getHits()).collect(Collectors.toList());
        searchAfterValues = lastHitSortFrom(hits);
        return hits;
    }

    private SearchResponse search(SearchRequest search) {
        configureSort(search.source());

        return client.search(search, "Failed to execute Search After request");
    }

    private void configureSort(SearchSourceBuilder source) {
        source.sort("timestamp", SortOrder.DESC);
        source.sort(DEFAULT_TIEBREAKER_FIELD, SortOrder.DESC);
    }

    private Object[] lastHitSortFrom(List<SearchHit> hits) {
        if (hits.isEmpty())
            return null;

        SearchHit lastHit = hits.get(hits.size() - 1);

        return lastHit.getSortValues();
    }

    @Override
    public SearchSourceBuilder configure(SearchSourceBuilder ssb) {
        return searchAfterValues == null ? ssb : ssb.searchAfter(searchAfterValues);
    }
}
