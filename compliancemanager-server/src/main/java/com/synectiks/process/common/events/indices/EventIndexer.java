/*
 * */
package com.synectiks.process.common.events.indices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.common.events.event.Event;
import com.synectiks.process.common.events.event.EventWithContext;
import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.messages.IndexingRequest;
import com.synectiks.process.server.indexer.messages.Messages;
import com.synectiks.process.server.plugin.database.Persisted;
import com.synectiks.process.server.streams.StreamService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class contains indices helper for the events system.
 */
@Singleton
public class EventIndexer {
    private static final Logger LOG = LoggerFactory.getLogger(EventIndexer.class);

    private final StreamService streamService;
    private final Messages messages;

    @Inject
    public EventIndexer(StreamService streamService, Messages messages) {
        this.streamService = streamService;
        this.messages = messages;
    }

    public void write(List<EventWithContext> eventsWithContext) {
        if (eventsWithContext.isEmpty()) {
            return;
        }

        // Pre-load all write index targets of all events to avoid looking them up for every event when building the bulk request
        final Set<String> streamIds = streamIdsForEvents(eventsWithContext);
        final Map<String, IndexSet> streamIndices = indexSetsForStreams(streamIds);
        final List<IndexingRequest> requests = eventsWithContext.stream()
                .map(EventWithContext::event)
                // Collect a set of indices for the event to avoid writing to the same index set twice if
                // multiple streams use the same index set.
                .flatMap(event -> assignEventsToTargetIndices(event, streamIndices))
                .map(event -> IndexingRequest.create(event.getKey(), event.getValue()))
                .collect(Collectors.toList());
        messages.bulkIndexRequests(requests, true);
    }

    private Map<String, IndexSet> indexSetsForStreams(Set<String> streamIds) {
        return streamService.loadByIds(streamIds).stream()
            .collect(Collectors.toMap(Persisted::getId, com.synectiks.process.server.plugin.streams.Stream::getIndexSet));
    }

    private Set<String> streamIdsForEvents(List<EventWithContext> eventsWithContext) {
        return eventsWithContext.stream()
            .map(EventWithContext::event)
            .flatMap(event -> event.getStreams().stream())
            .collect(Collectors.toSet());
    }

    private Stream<AbstractMap.SimpleEntry<IndexSet, Event>> assignEventsToTargetIndices(Event event, Map<String, IndexSet> streamIndices) {
        final Set<IndexSet> indices = indicesForEvent(event, streamIndices);
        return indices.stream()
                .map(index -> new AbstractMap.SimpleEntry<>(index, event));
    }

    private Set<IndexSet> indicesForEvent(Event event, Map<String, IndexSet> streamIndices) {
        return event.getStreams().stream()
                .map(streamId -> {
                    final IndexSet index = streamIndices.get(streamId);
                    if (index == null) {
                        LOG.warn("Couldn't find index set of stream <{}> for event <{}> (definition: {}/{})", streamId,
                                event.getId(), event.getEventDefinitionType(), event.getEventDefinitionId());
                    }
                    return index;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
