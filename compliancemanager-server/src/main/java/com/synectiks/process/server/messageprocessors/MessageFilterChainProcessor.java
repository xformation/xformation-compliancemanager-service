/*
 * */
package com.synectiks.process.server.messageprocessors;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.Messages;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.filters.MessageFilter;
import com.synectiks.process.server.plugin.messageprocessors.MessageProcessor;
import com.synectiks.process.server.shared.buffers.processors.ProcessBufferProcessor;
import com.synectiks.process.server.shared.journal.Journal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.codahale.metrics.MetricRegistry.name;

public class MessageFilterChainProcessor implements MessageProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(MessageFilterChainProcessor.class);

    public static class Descriptor implements MessageProcessor.Descriptor {
        @Override
        public String name() {
            return "Message Filter Chain";
        }

        @Override
        public String className() {
            return MessageFilterChainProcessor.class.getCanonicalName();
        }
    }

    private final List<MessageFilter> filterRegistry;
    private final MetricRegistry metricRegistry;
    private final Journal journal;
    private final ServerStatus serverStatus;
    private final Meter filteredOutMessages;

    @Inject
    public MessageFilterChainProcessor(MetricRegistry metricRegistry,
                                       Set<MessageFilter> filterRegistry,
                                       Journal journal,
                                       ServerStatus serverStatus) {
        this.metricRegistry = metricRegistry;
        this.journal = journal;
        this.serverStatus = serverStatus;
        // we need to keep this sorted properly, so that the filters run in the correct order
        this.filterRegistry = Ordering.from(new Comparator<MessageFilter>() {
            @Override
            public int compare(MessageFilter filter1, MessageFilter filter2) {
                return ComparisonChain.start()
                        .compare(filter1.getPriority(), filter2.getPriority())
                        .compare(filter1.getName(), filter2.getName())
                        .result();
            }
        }).immutableSortedCopy(filterRegistry);

        if (filterRegistry.size() == 0)
            throw new RuntimeException("Empty filter registry!");

        this.filteredOutMessages = metricRegistry.meter(name(ProcessBufferProcessor.class, "filteredOutMessages"));
    }

    @Override
    public Messages process(Messages messages) {

        for (final MessageFilter filter : filterRegistry) {
            for (Message msg : messages) {
                final String timerName = name(filter.getClass(), "executionTime");
                final Timer timer = metricRegistry.timer(timerName);
                final Timer.Context timerContext = timer.time();

                try {
                    LOG.debug("Applying filter [{}] on message <{}>.", filter.getName(), msg.getId());

                    if (filter.filter(msg)) {
                        LOG.debug("Filter [{}] marked message <{}> to be discarded. Dropping message.",
                                filter.getName(),
                                msg.getId());
                        msg.setFilterOut(true);
                        filteredOutMessages.mark();
                        journal.markJournalOffsetCommitted(msg.getJournalOffset());
                    }
                } catch (Exception e) {
                    LOG.error("Could not apply filter [" + filter.getName() + "] on message <" + msg.getId() + ">: ",
                            e);
                } finally {
                    final long elapsedNanos = timerContext.stop();
                    msg.recordTiming(serverStatus, timerName, elapsedNanos);
                }
            }
        }
        return messages;
    }

    @VisibleForTesting
    protected List<MessageFilter> getFilterRegistry() {
        return filterRegistry;
    }
}
