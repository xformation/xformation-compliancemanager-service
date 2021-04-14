/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.views.searchtypes;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.name.Named;
import com.synectiks.process.common.plugins.views.search.LegacyDecoratorProcessor;
import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.SearchJob;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.common.plugins.views.search.elasticsearch.ElasticsearchQueryString;
import com.synectiks.process.common.plugins.views.search.elasticsearch.QueryStringDecorators;
import com.synectiks.process.common.plugins.views.search.searchtypes.MessageList;
import com.synectiks.process.common.plugins.views.search.searchtypes.Sort;
import com.synectiks.process.common.storage.elasticsearch6.views.ESGeneratedQueryContext;
import com.synectiks.process.server.indexer.results.ResultMessage;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import com.synectiks.process.server.rest.models.messages.responses.ResultMessageSummary;
import com.synectiks.process.server.rest.resources.search.responses.SearchResponse;

import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.index.query.QueryBuilders;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.search.builder.SearchSourceBuilder;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.search.sort.FieldSortBuilder;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.search.sort.SortBuilders;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;

public class ESMessageList implements ESSearchTypeHandler<MessageList> {
    private final QueryStringDecorators esQueryDecorators;
    private final LegacyDecoratorProcessor decoratorProcessor;
    private final boolean allowHighlighting;

    @Inject
    public ESMessageList(QueryStringDecorators esQueryDecorators,
                         LegacyDecoratorProcessor decoratorProcessor,
                         @Named("allow_highlighting") boolean allowHighlighting) {
        this.esQueryDecorators = esQueryDecorators;
        this.decoratorProcessor = decoratorProcessor;
        this.allowHighlighting = allowHighlighting;
    }

    @VisibleForTesting
    public ESMessageList(QueryStringDecorators esQueryDecorators) {
        this(esQueryDecorators, new LegacyDecoratorProcessor.Fake(), false);
    }

    @Override
    public void doGenerateQueryPart(SearchJob job, Query query, MessageList messageList, ESGeneratedQueryContext queryContext) {

        final SearchSourceBuilder searchSourceBuilder = queryContext.searchSourceBuilder(messageList)
                .size(messageList.limit())
                .from(messageList.offset());

        applyHighlightingIfActivated(searchSourceBuilder, job, query);

        final Set<String> effectiveStreamIds = messageList.effectiveStreams().isEmpty()
                ? query.usedStreamIds()
                : messageList.effectiveStreams();

        final List<Sort> sorts = firstNonNull(messageList.sort(), Collections.singletonList(Sort.create(Message.FIELD_TIMESTAMP, Sort.Order.DESC)));
        sorts.forEach(sort -> {
            final FieldSortBuilder fieldSort = SortBuilders.fieldSort(sort.field())
                    .order(toSortOrder(sort.order()));
            final Optional<String> fieldType = queryContext.fieldType(effectiveStreamIds, sort.field());
            searchSourceBuilder.sort(fieldType.map(fieldSort::unmappedType).orElse(fieldSort));
        });
    }

    private SortOrder toSortOrder(Sort.Order sortOrder) {
        switch (sortOrder) {
            case ASC: return SortOrder.ASC;
            case DESC: return SortOrder.DESC;
            default: throw new IllegalStateException("Invalid sort order: " + sortOrder);
        }
    }
    private void applyHighlightingIfActivated(SearchSourceBuilder searchSourceBuilder, SearchJob job, Query query) {
        if (!allowHighlighting)
            return;

        final QueryStringQueryBuilder highlightQuery = decoratedHighlightQuery(job, query);

        searchSourceBuilder.highlighter(new HighlightBuilder().requireFieldMatch(false)
                .highlightQuery(highlightQuery)
                .field("*")
                .fragmentSize(0)
                .numOfFragments(0));
    }

    private QueryStringQueryBuilder decoratedHighlightQuery(SearchJob job, Query query) {
        final String raw = ((ElasticsearchQueryString) query.query()).queryString();

        final String decorated = this.esQueryDecorators.decorate(raw, job, query, Collections.emptySet());

        return QueryBuilders.queryStringQuery(decorated);
    }

    @Override
    public SearchType.Result doExtractResult(SearchJob job, Query query, MessageList searchType, SearchResult result, MetricAggregation aggregations, ESGeneratedQueryContext queryContext) {
        //noinspection unchecked
        final List<ResultMessageSummary> messages = result.getHits(Map.class, false).stream()
                .map(hit -> ResultMessage.parseFromSource(hit.id, hit.index, (Map<String, Object>) hit.source, hit.highlight))
                .map((resultMessage) -> ResultMessageSummary.create(resultMessage.highlightRanges, resultMessage.getMessage().getFields(), resultMessage.getIndex()))
                .collect(Collectors.toList());

        final String undecoratedQueryString = ((ElasticsearchQueryString)query.query()).queryString();
        final String queryString = this.esQueryDecorators.decorate(undecoratedQueryString, job, query, Collections.emptySet());

        final DateTime from = query.effectiveTimeRange(searchType).getFrom();
        final DateTime to = query.effectiveTimeRange(searchType).getTo();

        final SearchResponse searchResponse = SearchResponse.create(
                undecoratedQueryString,
                queryString,
                Collections.emptySet(),
                messages,
                Collections.emptySet(),
                0,
                result.getTotal(),
                from,
                to
        );

        final SearchResponse decoratedSearchResponse = decoratorProcessor.decorateSearchResponse(searchResponse, searchType.decorators());

        final MessageList.Result.Builder resultBuilder = MessageList.Result.result(searchType.id())
                .messages(decoratedSearchResponse.messages())
                .effectiveTimerange(AbsoluteRange.create(from, to))
                .totalResults(decoratedSearchResponse.totalResults());
        return searchType.name().map(resultBuilder::name).orElse(resultBuilder).build();
    }
}
