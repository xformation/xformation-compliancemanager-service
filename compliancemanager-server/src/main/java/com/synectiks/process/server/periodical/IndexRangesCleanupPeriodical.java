/*
 * */
package com.synectiks.process.server.periodical;

import com.github.joschi.jadconfig.util.Duration;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.google.common.primitives.Ints;
import com.google.inject.name.Named;
import com.synectiks.process.server.indexer.IndexSetRegistry;
import com.synectiks.process.server.indexer.cluster.Cluster;
import com.synectiks.process.server.indexer.indices.events.IndicesDeletedEvent;
import com.synectiks.process.server.indexer.ranges.IndexRange;
import com.synectiks.process.server.indexer.ranges.IndexRangeService;
import com.synectiks.process.server.plugin.periodical.Periodical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import static java.util.Objects.requireNonNull;

/**
 * A {@link Periodical} to clean up stale index ranges (e. g. because the index has been deleted externally)
 *
 * @since 1.3.0
 */
public class IndexRangesCleanupPeriodical extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(IndexRangesCleanupPeriodical.class);

    private final Cluster cluster;
    private final IndexSetRegistry indexSetRegistry;
    private final IndexRangeService indexRangeService;
    private final EventBus eventBus;
    private final int periodSeconds;

    @Inject
    public IndexRangesCleanupPeriodical(final Cluster cluster,
                                        final IndexSetRegistry indexSetRegistry,
                                        final IndexRangeService indexRangeService,
                                        final EventBus eventBus,
                                        @Named("index_ranges_cleanup_interval") final Duration indexRangesCleanupInterval) {
        this.cluster = requireNonNull(cluster);
        this.indexSetRegistry = requireNonNull(indexSetRegistry);
        this.indexRangeService = requireNonNull(indexRangeService);
        this.eventBus = requireNonNull(eventBus);
        this.periodSeconds = Ints.saturatedCast(indexRangesCleanupInterval.toSeconds());

    }

    @Override
    public void doRun() {
        if (!cluster.isConnected() || !cluster.isHealthy()) {
            LOG.info("Skipping index range cleanup because the Elasticsearch cluster is unreachable or unhealthy");
            return;
        }

        final Set<String> indexNames = ImmutableSet.copyOf(indexSetRegistry.getManagedIndices());
        final SortedSet<IndexRange> indexRanges = indexRangeService.findAll();

        final Set<String> removedIndices = new HashSet<>();
        for (IndexRange indexRange : indexRanges) {
            if (!indexNames.contains(indexRange.indexName())) {
                removedIndices.add(indexRange.indexName());
            }
        }

        if (!removedIndices.isEmpty()) {
            LOG.info("Removing index range information for unavailable indices: {}", removedIndices);
            eventBus.post(IndicesDeletedEvent.create(removedIndices));
        }
    }

    @Override
    public boolean runsForever() {
        return false;
    }

    @Override
    public boolean stopOnGracefulShutdown() {
        return true;
    }

    @Override
    public boolean masterOnly() {
        return true;
    }

    @Override
    public boolean startOnThisNode() {
        return true;
    }

    @Override
    public boolean isDaemon() {
        return true;
    }

    @Override
    public int getInitialDelaySeconds() {
        return 15;
    }

    @Override
    public int getPeriodSeconds() {
        return periodSeconds;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
