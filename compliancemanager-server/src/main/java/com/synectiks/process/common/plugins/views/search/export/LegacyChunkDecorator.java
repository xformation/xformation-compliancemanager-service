/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import com.google.common.collect.ImmutableMultimap;
import com.synectiks.process.common.plugins.views.search.LegacyDecoratorProcessor;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;
import com.synectiks.process.server.rest.models.messages.responses.ResultMessageSummary;
import com.synectiks.process.server.rest.resources.search.responses.SearchResponse;

import javax.inject.Inject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class LegacyChunkDecorator implements ChunkDecorator {

    private final LegacyDecoratorProcessor decoratorProcessor;

    @Inject
    public LegacyChunkDecorator(LegacyDecoratorProcessor decoratorProcessor) {
        this.decoratorProcessor = decoratorProcessor;
    }

    @Override
    public SimpleMessageChunk decorate(SimpleMessageChunk undecoratedChunk, ExportMessagesCommand command) {

        SearchResponse undecoratedLegacyResponse = legacySearchResponseFrom(undecoratedChunk, command);

        SearchResponse decoratedLegacyResponse = decoratorProcessor.decorateSearchResponse(undecoratedLegacyResponse, command.decorators());

        SimpleMessageChunk decoratedChunk = simpleMessageChunkFrom(decoratedLegacyResponse, undecoratedChunk.fieldsInOrder());

        return decoratedChunk.toBuilder().isFirstChunk(undecoratedChunk.isFirstChunk()).build();
    }

    private SimpleMessageChunk simpleMessageChunkFrom(SearchResponse searchResponse, LinkedHashSet<String> fieldsInOrder) {
        LinkedHashSet<SimpleMessage> messages = searchResponse.messages().stream()
                .map(legacyMessage -> SimpleMessage.from(legacyMessage.index(), new LinkedHashMap<String, Object>(legacyMessage.message())))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // use fieldsInOrder from undecoratedChunk, because the order can get mixed up when decorators are applied
        return SimpleMessageChunk.from(fieldsInOrder, messages);
    }

    private SearchResponse legacySearchResponseFrom(SimpleMessageChunk chunk, ExportMessagesCommand command) {
        final List<ResultMessageSummary> legacyMessages = legacyMessagesFrom(chunk);

        String queryString = command.queryString().queryString();
        TimeRange timeRange = command.timeRange();
        return SearchResponse.create(
                queryString,
                queryString,
                Collections.emptySet(),
                legacyMessages,
                chunk.fieldsInOrder(),
                -1,
                -1,
                timeRange.getFrom(),
                timeRange.getTo()
        );
    }

    private List<ResultMessageSummary> legacyMessagesFrom(SimpleMessageChunk chunk) {
        return chunk.messages().stream()
                .map(simpleMessage -> ResultMessageSummary.create(ImmutableMultimap.of(), simpleMessage.fields(), simpleMessage.index()))
                .collect(Collectors.toList());
    }
}
