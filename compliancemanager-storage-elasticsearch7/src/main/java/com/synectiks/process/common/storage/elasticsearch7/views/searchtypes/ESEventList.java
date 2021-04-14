/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7.views.searchtypes;

import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.SearchJob;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.common.plugins.views.search.searchtypes.events.EventList;
import com.synectiks.process.common.plugins.views.search.searchtypes.events.EventSummary;
import com.synectiks.process.common.storage.elasticsearch7.views.ESGeneratedQueryContext;

import org.graylog.shaded.elasticsearch7.org.elasticsearch.action.search.SearchResponse;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.search.SearchHit;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.search.aggregations.Aggregations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ESEventList implements ESSearchTypeHandler<EventList> {
    @Override
    public void doGenerateQueryPart(SearchJob job, Query query, EventList eventList,
                                    ESGeneratedQueryContext queryContext) {
        queryContext.searchSourceBuilder(eventList)
                .size(10000);
    }

    protected List<Map<String, Object>> extractResult(SearchResponse result) {
        return StreamSupport.stream(result.getHits().spliterator(), false)
                .map(SearchHit::getSourceAsMap)
                .collect(Collectors.toList());
    }

    @Override
    public SearchType.Result doExtractResult(SearchJob job, Query query, EventList searchType, SearchResponse result,
                                             Aggregations aggregations, ESGeneratedQueryContext queryContext) {
        final Set<String> effectiveStreams = searchType.streams().isEmpty()
                ? query.usedStreamIds()
                : searchType.streams();
        final List<EventSummary> eventSummaries = extractResult(result).stream()
                .map(EventSummary::parse)
                .filter(eventSummary -> effectiveStreams.containsAll(eventSummary.streams()))
                .collect(Collectors.toList());
        final EventList.Result.Builder resultBuilder = EventList.Result.builder()
                .events(eventSummaries)
                .id(searchType.id());
        searchType.name().ifPresent(resultBuilder::name);
        return resultBuilder
                .build();
    }
}
