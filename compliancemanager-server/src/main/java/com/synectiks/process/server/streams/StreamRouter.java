/*
 * */
package com.synectiks.process.server.streams;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.synectiks.process.server.indexer.indexset.events.IndexSetCreatedEvent;
import com.synectiks.process.server.indexer.indexset.events.IndexSetDeletedEvent;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.streams.events.StreamsChangedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Routes a {@link com.synectiks.process.server.plugin.Message} to its streams.
 */
public class StreamRouter {
    private static final Logger LOG = LoggerFactory.getLogger(StreamRouter.class);

    private final ServerStatus serverStatus;
    private final ScheduledExecutorService scheduler;

    private final AtomicReference<StreamRouterEngine> routerEngine = new AtomicReference<>(null);
    private final StreamRouterEngineUpdater engineUpdater;

    @Inject
    public StreamRouter(StreamService streamService,
                        ServerStatus serverStatus,
                        StreamRouterEngine.Factory routerEngineFactory,
                        EventBus serverEventBus,
                        @Named("daemonScheduler") ScheduledExecutorService scheduler) {
        this.serverStatus = serverStatus;
        this.scheduler = scheduler;

        this.engineUpdater = new StreamRouterEngineUpdater(routerEngine, routerEngineFactory, streamService, executorService());
        this.routerEngine.set(engineUpdater.getNewEngine());

        // TODO: This class needs lifecycle management to avoid leaking objects in the EventBus
        serverEventBus.register(this);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handleStreamsUpdate(StreamsChangedEvent event) {
        scheduler.submit(engineUpdater);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handleIndexSetCreation(IndexSetCreatedEvent event) {
        scheduler.submit(engineUpdater);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void handleIndexSetDeletion(IndexSetDeletedEvent event) {
        scheduler.submit(engineUpdater);
    }

    private ExecutorService executorService() {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("stream-router-%d")
                .setDaemon(true)
                .build();
        return Executors.newCachedThreadPool(threadFactory);
    }

    public List<Stream> route(final Message msg) {
        final StreamRouterEngine engine = routerEngine.get();

        msg.recordCounter(serverStatus, "streams-evaluated", engine.getStreams().size());

        return engine.match(msg);
    }

    private static class StreamRouterEngineUpdater implements Runnable {
        private final AtomicReference<StreamRouterEngine> routerEngine;
        private final StreamRouterEngine.Factory engineFactory;
        private final StreamService streamService;
        private final ExecutorService executorService;

        public StreamRouterEngineUpdater(AtomicReference<StreamRouterEngine> routerEngine,
                                         StreamRouterEngine.Factory engineFactory,
                                         StreamService streamService,
                                         ExecutorService executorService) {
            this.routerEngine = routerEngine;
            this.engineFactory = engineFactory;
            this.streamService = streamService;
            this.executorService = executorService;
        }

        @Override
        public void run() {
            try {
                final StreamRouterEngine engine = getNewEngine();

                if (engine.getFingerprint().equals(routerEngine.get().getFingerprint())) {
                    LOG.debug("Not updating router engine, streams did not change (fingerprint={})", engine.getFingerprint());
                } else {
                    LOG.debug("Updating to new stream router engine. (old-fingerprint={} new-fingerprint={}",
                            routerEngine.get().getFingerprint(), engine.getFingerprint());
                    routerEngine.set(engine);
                }
            } catch (Exception e) {
                LOG.error("Stream router engine update failed!", e);
            }
        }

        private StreamRouterEngine getNewEngine() {
            return engineFactory.create(streamService.loadAllEnabled(), executorService);
        }
    }
}
