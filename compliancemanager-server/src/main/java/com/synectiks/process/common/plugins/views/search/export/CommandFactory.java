/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.Search;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.common.plugins.views.search.elasticsearch.ElasticsearchQueryString;
import com.synectiks.process.common.plugins.views.search.searchtypes.MessageList;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import javax.inject.Inject;
import java.util.Set;

public class CommandFactory {
    private final QueryStringDecorator queryStringDecorator;

    @Inject
    public CommandFactory(QueryStringDecorator queryStringDecorator) {
        this.queryStringDecorator = queryStringDecorator;
    }

    public ExportMessagesCommand buildFromRequest(MessagesRequest request) {
        ExportMessagesCommand.Builder builder = ExportMessagesCommand.builder()
                .timeRange(toAbsolute(request.timeRange()))
                .queryString(request.queryString())
                .streams(request.streams())
                .fieldsInOrder(request.fieldsInOrder())
                .chunkSize(request.chunkSize());

        if (request.limit().isPresent()) {
            builder.limit(request.limit().getAsInt());
        }

        return builder.build();
    }

    public ExportMessagesCommand buildWithSearchOnly(Search search, ResultFormat resultFormat) {
        Query query = queryFrom(search);

        return builderFrom(resultFormat)
                .timeRange(toAbsolute(query.timerange()))
                .queryString(queryStringFrom(search, query))
                .streams(query.usedStreamIds())
                .build();
    }

    private Query queryFrom(Search s) {
        if (s.queries().size() > 1) {
            throw new ExportException("Can't get messages for search with id " + s.id() + ", because it contains multiple queries");
        }

        return s.queries().stream().findFirst()
                .orElseThrow(() -> new ExportException("Invalid Search object with empty Query"));
    }

    public ExportMessagesCommand buildWithMessageList(Search search, String messageListId, ResultFormat resultFormat) {
        Query query = search.queryForSearchType(messageListId);
        MessageList messageList = messageListFrom(query, messageListId);

        ExportMessagesCommand.Builder commandBuilder = builderFrom(resultFormat)
                .timeRange(toAbsolute(timeRangeFrom(query, messageList)))
                .queryString(queryStringFrom(search, query, messageList))
                .streams(streamsFrom(query, messageList))
                .decorators(messageList.decorators());

        return commandBuilder.build();
    }

    private MessageList messageListFrom(Query query, String searchTypeId) {
        SearchType searchType = query.searchTypes().stream()
                .filter(st -> st.id().equals(searchTypeId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Error getting search type"));

        if (!(searchType instanceof MessageList)) {
            throw new ExportException("export is not supported for search type " + searchType.getClass());
        }
        return (MessageList) searchType;
    }

    private AbsoluteRange toAbsolute(TimeRange timeRange) {
        return AbsoluteRange.create(timeRange.getFrom(), timeRange.getTo());
    }

    private ExportMessagesCommand.Builder builderFrom(ResultFormat resultFormat) {
        ExportMessagesCommand.Builder requestBuilder = ExportMessagesCommand.builder();

        requestBuilder.fieldsInOrder(resultFormat.fieldsInOrder());

        if (resultFormat.limit().isPresent()) {
            requestBuilder.limit(resultFormat.limit().getAsInt());
        }

        return requestBuilder;
    }

    private TimeRange timeRangeFrom(Query query, MessageList ml) {
        if (ml.timerange().isPresent()) {
            return query.effectiveTimeRange(ml);
        } else {
            return query.timerange();
        }
    }

    private ElasticsearchQueryString queryStringFrom(Search search, Query query) {
        ElasticsearchQueryString undecorated = queryStringFrom(query);
        return decorateQueryString(search, query, undecorated);
    }

    private ElasticsearchQueryString queryStringFrom(Search search, Query query, MessageList messageList) {
        ElasticsearchQueryString undecorated = pickQueryString(messageList, query);
        return decorateQueryString(search, query, undecorated);
    }

    private ElasticsearchQueryString pickQueryString(MessageList messageList, Query query) {
        if (messageList.query().isPresent() && hasQueryString(query)) {
            return esQueryStringFrom(query).concatenate(esQueryStringFrom(messageList));
        } else if (messageList.query().isPresent()) {
            return esQueryStringFrom(messageList);
        } else {
            return queryStringFrom(query);
        }
    }

    private boolean hasQueryString(Query query) {
        return query.query() instanceof ElasticsearchQueryString;
    }

    private ElasticsearchQueryString queryStringFrom(Query query) {
        return hasQueryString(query) ? esQueryStringFrom(query) : ElasticsearchQueryString.empty();
    }

    private ElasticsearchQueryString esQueryStringFrom(MessageList ml) {
        //noinspection OptionalGetWithoutIsPresent
        return (ElasticsearchQueryString) ml.query().get();
    }

    private ElasticsearchQueryString esQueryStringFrom(Query query) {
        return (ElasticsearchQueryString) query.query();
    }

    private ElasticsearchQueryString decorateQueryString(Search search, Query query, ElasticsearchQueryString undecorated) {
        String queryString = undecorated.queryString();
        String decorated = queryStringDecorator.decorateQueryString(queryString, search, query);
        return ElasticsearchQueryString.builder().queryString(decorated).build();
    }

    private Set<String> streamsFrom(Query query, MessageList messageList) {
        return messageList.effectiveStreams().isEmpty() ?
                query.usedStreamIds() :
                messageList.effectiveStreams();
    }
}
