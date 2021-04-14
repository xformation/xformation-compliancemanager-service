/*
 * */
package com.synectiks.process.server.indexer.fieldtypes;

import com.github.joschi.jadconfig.util.Duration;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.synectiks.process.server.indexer.MongoIndexSet;
import com.synectiks.process.server.indexer.cluster.Cluster;
import com.synectiks.process.server.indexer.fieldtypes.IndexFieldTypePoller;
import com.synectiks.process.server.indexer.fieldtypes.IndexFieldTypePollerPeriodical;
import com.synectiks.process.server.indexer.fieldtypes.IndexFieldTypesService;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indexset.IndexSetService;
import com.synectiks.process.server.indexer.indexset.events.IndexSetCreatedEvent;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.lifecycles.Lifecycle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class IndexFieldTypePollerPeriodicalTest {
    private IndexFieldTypePollerPeriodical periodical;
    private final IndexFieldTypePoller indexFieldTypePoller = mock(IndexFieldTypePoller.class);
    private final IndexFieldTypesService indexFieldTypesService = mock(IndexFieldTypesService.class);
    private final IndexSetService indexSetService = mock(IndexSetService.class);
    private final Indices indices = mock(Indices.class);
    private final MongoIndexSet.Factory mongoIndexSetFactory = mock(MongoIndexSet.Factory.class);
    private final Cluster cluster = mock(Cluster.class);
    @SuppressWarnings("UnstableApiUsage")
    private final EventBus eventBus = mock(EventBus.class);
    private final ServerStatus serverStatus = mock(ServerStatus.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder().setNameFormat("index-field-type-poller-periodical-test-%d").build()
    );

    @BeforeEach
    void setUp() {
        this.periodical = new IndexFieldTypePollerPeriodical(indexFieldTypePoller,
                indexFieldTypesService,
                indexSetService,
                indices,
                mongoIndexSetFactory,
                cluster,
                eventBus,
                serverStatus,
                Duration.seconds(30),
                scheduler);
    }

    @Test
    void scheduledExecutionIsSkippedWhenServerIsNotRunning() throws InterruptedException {
        when(serverStatus.getLifecycle()).thenReturn(Lifecycle.HALTING);
        final IndexSetConfig indexSetConfig = mockIndexSetConfig();
        final IndexSetCreatedEvent indexSetCreatedEvent = IndexSetCreatedEvent.create(indexSetConfig);

        when(indexSetService.get("foo")).thenReturn(Optional.of(indexSetConfig));

        final MongoIndexSet mongoIndexSet = mockMongoIndexSet(indexSetConfig);
        when(mongoIndexSetFactory.create(indexSetConfig)).thenReturn(mongoIndexSet);

        this.periodical.handleIndexSetCreation(indexSetCreatedEvent);

        Thread.sleep(100);

        verifyNoInteractions(indexFieldTypePoller);
    }

    private MongoIndexSet mockMongoIndexSet(IndexSetConfig indexSetConfig) {
        final MongoIndexSet mongoIndexSet = mock(MongoIndexSet.class);
        when(mongoIndexSet.getConfig()).thenReturn(indexSetConfig);
        when(mongoIndexSet.getActiveWriteIndex()).thenReturn("foo-0");
        return mongoIndexSet;
    }

    private IndexSetConfig mockIndexSetConfig() {
        final IndexSetConfig indexSetConfig = mock(IndexSetConfig.class);
        when(indexSetConfig.id()).thenReturn("foo");
        when(indexSetConfig.fieldTypeRefreshInterval()).thenReturn(new org.joda.time.Duration(1L));
        when(indexSetConfig.isWritable()).thenReturn(true);
        return indexSetConfig;
    }
}
