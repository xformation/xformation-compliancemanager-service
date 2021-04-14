/*
 * */
package com.synectiks.process.server.outputs;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.notifications.Notification;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.plugin.outputs.MessageOutput;
import com.synectiks.process.server.plugin.outputs.MessageOutputConfigurationException;
import com.synectiks.process.server.plugin.streams.Output;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.streams.OutputService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class OutputRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(OutputRegistry.class);

    private final Cache<String, MessageOutput> runningMessageOutputs;
    private final MessageOutput defaultMessageOutput;
    private final OutputService outputService;
    private final NotificationService notificationService;
    private final NodeId nodeId;
    private final MessageOutputFactory messageOutputFactory;
    private final LoadingCache<String, AtomicInteger> faultCounters;
    private final long faultCountThreshold;
    private final long faultPenaltySeconds;

    @Inject
    public OutputRegistry(@DefaultMessageOutput MessageOutput defaultMessageOutput,
                          OutputService outputService,
                          MessageOutputFactory messageOutputFactory,
                          NotificationService notificationService,
                          NodeId nodeId,
                          @Named("output_fault_count_threshold") long faultCountThreshold,
                          @Named("output_fault_penalty_seconds") long faultPenaltySeconds) {
        this.defaultMessageOutput = defaultMessageOutput;
        this.outputService = outputService;
        this.notificationService = notificationService;
        this.nodeId = nodeId;
        this.messageOutputFactory = messageOutputFactory;
        this.runningMessageOutputs = CacheBuilder.newBuilder().build();
        this.faultCountThreshold = faultCountThreshold;
        this.faultPenaltySeconds = faultPenaltySeconds;
        this.faultCounters = CacheBuilder.newBuilder()
                .expireAfterWrite(this.faultPenaltySeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, AtomicInteger>() {
                    @Override
                    public AtomicInteger load(String key) throws Exception {
                        return new AtomicInteger(0);
                    }
                });
    }

    @Nullable
    public MessageOutput getOutputForIdAndStream(String id, Stream stream) {
        final AtomicInteger faultCount;
        try {
            faultCount = this.faultCounters.get(id);
        } catch (ExecutionException e) {
            LOG.error("Unable to retrieve output fault counter", e);
            return null;
        }

        try {
            if (faultCount.get() < faultCountThreshold) {
                return this.runningMessageOutputs.get(id, loadForIdAndStream(id, stream));
            }
        } catch (ExecutionException | UncheckedExecutionException e) {
            if (!(e.getCause() instanceof NotFoundException)) {
                final int number = faultCount.addAndGet(1);
                LOG.error("Unable to fetch output " + id + ", fault #" + number, e);
                if (number >= faultCountThreshold) {
                    LOG.error("Output {} has crossed threshold of {} faults in {} seconds. Disabling for {} seconds.",
                            id,
                            faultCountThreshold,
                            faultPenaltySeconds,
                            faultPenaltySeconds);

                    final Notification notification = notificationService.buildNow()
                            .addType(Notification.Type.OUTPUT_DISABLED)
                            .addSeverity(Notification.Severity.NORMAL)
                            .addNode(nodeId.toString())
                            .addDetail("outputId", id)
                            .addDetail("streamId", stream.getId())
                            .addDetail("streamTitle", stream.getTitle())
                            .addDetail("faultCount", number)
                            .addDetail("faultCountThreshold", faultCountThreshold)
                            .addDetail("faultPenaltySeconds", faultPenaltySeconds);
                    notificationService.publishIfFirst(notification);
                }
            }
        }
        return null;
    }

    public Callable<MessageOutput> loadForIdAndStream(final String id, final Stream stream) {
        return new Callable<MessageOutput>() {
            @Override
            public MessageOutput call() throws Exception {
                final Output output = outputService.load(id);
                return launchOutput(output, stream);
            }
        };
    }

    protected MessageOutput launchOutput(Output output, Stream stream) throws MessageOutputConfigurationException {
        final MessageOutput messageOutput = messageOutputFactory.fromStreamOutput(output, stream,
                new com.synectiks.process.server.plugin.configuration.Configuration(output.getConfiguration()));

        if (messageOutput == null) {
            throw new IllegalArgumentException("Failed to instantiate MessageOutput from Output: " + output);
        }

        return messageOutput;
    }

    @VisibleForTesting
    protected Map<String, MessageOutput> getRunningMessageOutputs() {
        return ImmutableMap.copyOf(runningMessageOutputs.asMap());
    }

    public Set<MessageOutput> getMessageOutputs() {
        return ImmutableSet.<MessageOutput>builder()
                .addAll(runningMessageOutputs.asMap().values())
                .add(defaultMessageOutput)
                .build();
    }

    public void removeOutput(Output output) {
        final MessageOutput messageOutput = runningMessageOutputs.getIfPresent(output.getId());
        if (messageOutput != null) {
            messageOutput.stop();
        }

        runningMessageOutputs.invalidate(output.getId());
        faultCounters.invalidate(output.getId());
    }
}
