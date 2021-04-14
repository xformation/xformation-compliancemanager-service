/*
 * */
package com.synectiks.process.server.filters;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.inputs.Input;
import com.synectiks.process.server.inputs.InputService;
import com.synectiks.process.server.inputs.extractors.events.ExtractorCreated;
import com.synectiks.process.server.inputs.extractors.events.ExtractorDeleted;
import com.synectiks.process.server.inputs.extractors.events.ExtractorUpdated;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.filters.MessageFilter;
import com.synectiks.process.server.plugin.inputs.Extractor;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputCreated;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputDeleted;
import com.synectiks.process.server.rest.models.system.inputs.responses.InputUpdated;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class ExtractorFilter implements MessageFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ExtractorFilter.class);
    private static final String NAME = "Extractor";

    private final ConcurrentMap<String, List<Extractor>> extractors = new ConcurrentHashMap<>();

    private final InputService inputService;
    private final ScheduledExecutorService scheduler;

    @Inject
    public ExtractorFilter(InputService inputService,
                           EventBus serverEventBus,
                           @Named("daemonScheduler") ScheduledExecutorService scheduler) {
        this.inputService = inputService;
        this.scheduler = scheduler;

        loadAllExtractors();

        // TODO: This class needs lifecycle management to avoid leaking objects in the EventBus
        serverEventBus.register(this);
    }

    @Override
    public boolean filter(Message msg) {
        if (msg.getSourceInputId() == null) {
            return false;
        }

        for (final Extractor extractor : extractors.getOrDefault(msg.getSourceInputId(), Collections.emptyList())) {
            try {
                extractor.runExtractor(msg);
            } catch (Exception e) {
                extractor.incrementExceptions();
                LOG.error("Could not apply extractor \"" + extractor.getTitle() + "\" (id=" + extractor.getId() + ") "
                        + "to message " + msg.getId(), e);
            }
        }

        return false;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handleInputCreate(final InputCreated event) {
        LOG.debug("Load extractors for input <{}>", event.id());
        scheduler.submit(() -> loadExtractors(event.id()));
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handleInputDelete(final InputDeleted event) {
        LOG.debug("Removing input from extractors cache <{}>", event.id());
        extractors.remove(event.id());
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handleInputUpdate(final InputUpdated event) {
        LOG.debug("Updating extractors cache for input <{}>", event.id());
        scheduler.submit(() -> loadExtractors(event.id()));
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handleExtractorCreate(final ExtractorCreated event) {
        LOG.debug("Load extractors for input <{}>", event.inputId());
        scheduler.submit(() -> loadExtractors(event.inputId()));
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handleExtractorDelete(final ExtractorDeleted event) {
        LOG.debug("Removing extractors for input <{}> from extractors cache", event.inputId());
        scheduler.submit(() -> loadExtractors(event.inputId()   ));
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handleExtractorUpdate(final ExtractorUpdated event) {
        LOG.debug("Updating extractors cache for input <{}>", event.inputId());
        scheduler.submit(() -> loadExtractors(event.inputId()));
    }

    private void loadAllExtractors() {
        try {
            inputService.all().forEach(input -> loadExtractors(input.getId()));
        } catch (Exception e) {
            LOG.error("Unable to load extractors for all inputs", e);
        }
    }

    private void loadExtractors(final String inputId) {
        LOG.debug("Re-loading extractors for input <{}>", inputId);

        try {
            final Input input = inputService.find(inputId);
            final List<Extractor> sortedExtractors = inputService.getExtractors(input).stream()
                    .sorted(Comparator.comparingLong(Extractor::getOrder))
                    .collect(Collectors.toList());

            extractors.put(inputId, ImmutableList.copyOf(sortedExtractors));
        } catch (NotFoundException e) {
            LOG.warn("Unable to load input <{}>: {}", inputId, e.getMessage());
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getPriority() {
        // runs first of the built-in filters
        return 10;
    }

}
