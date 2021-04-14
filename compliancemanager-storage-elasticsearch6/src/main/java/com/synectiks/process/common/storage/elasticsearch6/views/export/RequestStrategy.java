/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.views.export;

import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.search.builder.SearchSourceBuilder;

import com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RequestStrategy {
    @SuppressWarnings("rawtypes")
    List<SearchResult.Hit<Map, Void>> nextChunk(Search.Builder search, ExportMessagesCommand command);

    /**
     * Allows implementers to specify options on SearchSourceBuilder that cannot be specified on Search.Builder.
     *
     * @see #nextChunk(Search.Builder, ExportMessagesCommand)
     * @see org.graylog.shaded.elasticsearch5.org.elasticsearch.search.builder.SearchSourceBuilder#searchAfter(Object[])
     */
    default SearchSourceBuilder configure(SearchSourceBuilder ssb) {
        return ssb;
    }

    /**
     * Overriding this allows implementers to remove streams containing messages that would can't be processed.
     * Most prominently those that are lacking the required tie breaker field for search-after.
     *
     * @param streams Streams the user wants to export
     * @return Streams that can be exported using the implementing RequestStrategy
     */
    default Set<String> removeUnsupportedStreams(Set<String> streams) {
        return streams;
    }
}
