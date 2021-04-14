/*
 * */
package com.synectiks.process.server.decorators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.decorators.SearchResponseDecorator;
import com.synectiks.process.server.rest.models.messages.responses.DecorationStats;
import com.synectiks.process.server.rest.models.messages.responses.ResultMessageSummary;
import com.synectiks.process.server.rest.resources.search.responses.SearchDecorationStats;
import com.synectiks.process.server.rest.resources.search.responses.SearchResponse;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DecoratorProcessorImpl implements DecoratorProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(DecoratorProcessorImpl.class);

    private final DecoratorResolver decoratorResolver;

    @Inject
    public DecoratorProcessorImpl(DecoratorResolver decoratorResolver) {
        this.decoratorResolver = decoratorResolver;
    }

    @Override
    public SearchResponse decorate(SearchResponse searchResponse, Optional<String> streamId) {
        final List<SearchResponseDecorator> searchResponseDecorators = streamId.isPresent() ?
                decoratorResolver.searchResponseDecoratorsForStream(streamId.get()) : decoratorResolver.searchResponseDecoratorsForGlobal();
        return decorate(searchResponse, searchResponseDecorators);
    }

    @Override
    public SearchResponse decorate(SearchResponse searchResponse, List<SearchResponseDecorator> searchResponseDecorators) {
        try {
            final Optional<SearchResponseDecorator> metaDecorator = searchResponseDecorators.stream()
                    .reduce((f, g) -> (v) -> g.apply(f.apply(v)));
            if (metaDecorator.isPresent()) {
                final Map<String, ResultMessageSummary> originalMessages = searchResponse.messages()
                        .stream()
                        .collect(Collectors.toMap(this::getMessageKey, Function.identity()));
                final SearchResponse newSearchResponse = metaDecorator.get().apply(searchResponse);
                final Set<String> newFields = extractFields(newSearchResponse.messages());

                final List<ResultMessageSummary> decoratedMessages = newSearchResponse.messages()
                        .stream()
                        .map(resultMessage -> {
                            final ResultMessageSummary originalMessage = originalMessages.get(getMessageKey(resultMessage));
                            if (originalMessage != null) {
                                return resultMessage
                                        .toBuilder()
                                        .decorationStats(DecorationStats.create(originalMessage.message(), resultMessage.message()))
                                        .build();
                            }
                            return resultMessage;
                        })
                        .collect(Collectors.toList());

                return newSearchResponse
                        .toBuilder()
                        .messages(decoratedMessages)
                        .fields(newFields)
                        .decorationStats(this.getSearchDecoratorStats(decoratedMessages))
                        .build();
            }
        } catch (Exception e) {
            LOG.error("Error decorating search response", e);
        }

        return searchResponse;
    }

    private String getMessageKey(ResultMessageSummary messageSummary) {
        // Use index and message ID as key to allow the same message ID from different indices.
        // This will happen when the same message is indexed into different index sets.
        return messageSummary.index() + "-" + messageSummary.message().get("_id").toString();
    }

    private Set<String> extractFields(List<ResultMessageSummary> messages) {
        return messages.stream()
                .flatMap(message -> message.message().keySet().stream())
                .filter(field -> !Message.FILTERED_FIELDS.contains(field))
                .collect(Collectors.toSet());
    }

    private SearchDecorationStats getSearchDecoratorStats(List<ResultMessageSummary> decoratedMessages) {
        final Set<String> addedFields = new HashSet<>();
        final Set<String> changedFields = new HashSet<>();
        final Set<String> removedFields = new HashSet<>();

        decoratedMessages.forEach(message -> {
            final DecorationStats decorationStats = message.decorationStats();
            if (decorationStats != null) {
                addedFields.addAll(decorationStats.addedFields().keySet());
                changedFields.addAll(decorationStats.changedFields().keySet());
                removedFields.addAll(decorationStats.removedFields().keySet());
            }
        });

        return SearchDecorationStats.create(addedFields, changedFields, removedFields);
    }
}
