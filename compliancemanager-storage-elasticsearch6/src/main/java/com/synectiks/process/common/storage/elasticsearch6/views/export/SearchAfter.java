/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.views.export;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand;
import com.synectiks.process.server.plugin.Message;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.sort.Sort;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.synectiks.process.server.plugin.streams.Stream.DEFAULT_EVENT_STREAM_IDS;

public class SearchAfter implements RequestStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(SearchAfter.class);

    static final String DEFAULT_TIEBREAKER_FIELD = Message.FIELD_XFALERT_MESSAGE_ID;
    static final String EVENTS_TIEBREAKER_FIELD = Message.FIELD_ID;

    private final JestWrapper jestWrapper;

    private Object[] searchAfterValues = null;

    @Inject
    public SearchAfter(JestWrapper jestWrapper) {
        this.jestWrapper = jestWrapper;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<SearchResult.Hit<Map, Void>> nextChunk(Search.Builder search, ExportMessagesCommand command) {

        SearchResult result = search(search, command);
        List<SearchResult.Hit<Map, Void>> hits = result.getHits(Map.class, false);
        searchAfterValues = lastHitSortFrom(hits);
        return hits;
    }

    private SearchResult search(Search.Builder search, ExportMessagesCommand command) {
        Search.Builder modified = search.addSort(configureSort(command));

        return jestWrapper.execute(modified.build(), () -> "Failed to execute Search After request");
    }

    private ArrayList<Sort> configureSort(ExportMessagesCommand command) {
        return newArrayList(
                new Sort("timestamp", Sort.Sorting.DESC),
                new Sort(tieBreakerFrom(command.streams()), Sort.Sorting.DESC)
        );
    }

    private String tieBreakerFrom(Set<String> streams) {
        boolean hasOnlyEventStreams = Sets.difference(streams, DEFAULT_EVENT_STREAM_IDS).size() == 0;
        return hasOnlyEventStreams ? EVENTS_TIEBREAKER_FIELD : DEFAULT_TIEBREAKER_FIELD;
    }

    @SuppressWarnings("rawtypes")
    private Object[] lastHitSortFrom(List<SearchResult.Hit<Map, Void>> hits) {
        if (hits.isEmpty())
            return null;

        SearchResult.Hit<Map, Void> lastHit = hits.get(hits.size() - 1);

        return lastHit.sort.toArray(new Object[0]);
    }

    @Override
    public SearchSourceBuilder configure(SearchSourceBuilder ssb) {
        return searchAfterValues == null ? ssb : ssb.searchAfter(searchAfterValues);
    }

    @Override
    public Set<String> removeUnsupportedStreams(Set<String> streams) {
        boolean hasEventStreams = Sets.intersection(streams, DEFAULT_EVENT_STREAM_IDS).size() > 0;
        Sets.SetView<String> others = Sets.difference(streams, DEFAULT_EVENT_STREAM_IDS);
        boolean hasOthers = others.size() > 0;

        if (hasEventStreams && hasOthers) {
            LOG.warn("Search After requests for a mix of event streams and others are not supported. Removing event streams.");
            return ImmutableSet.copyOf(others);
        }

        return streams;
    }
}
