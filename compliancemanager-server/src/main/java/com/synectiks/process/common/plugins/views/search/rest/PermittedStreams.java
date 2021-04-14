/*
 * */
package com.synectiks.process.common.plugins.views.search.rest;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.streams.StreamService;

import javax.inject.Inject;
import java.util.Set;
import java.util.function.Predicate;

import static com.synectiks.process.server.plugin.streams.Stream.DEFAULT_EVENT_STREAM_IDS;
import static java.util.stream.Collectors.toSet;

public class PermittedStreams {
    private final StreamService streamService;

    @Inject
    public PermittedStreams(StreamService streamService) {
        this.streamService = streamService;
    }

    public ImmutableSet<String> load(Predicate<String> isStreamIdPermitted) {
        final Set<String> result = streamService.loadAll().stream()
                .map(com.synectiks.process.server.plugin.streams.Stream::getId)
                // Unless explicitly queried, exclude event indices by default
                // Having the event indices in every search, makes sorting almost impossible
                .filter(id -> !DEFAULT_EVENT_STREAM_IDS.contains(id))
                .filter(isStreamIdPermitted)
                .collect(toSet());

        return ImmutableSet.copyOf(result);
    }
}
