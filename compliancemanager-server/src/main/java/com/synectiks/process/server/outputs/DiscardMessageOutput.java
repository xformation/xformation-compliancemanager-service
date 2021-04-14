/*
 * */
package com.synectiks.process.server.outputs;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.outputs.MessageOutput;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.shared.journal.Journal;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.codahale.metrics.MetricRegistry.name;

public class DiscardMessageOutput implements MessageOutput {
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final Journal journal;
    private final Meter messagesDiscarded;

    @AssistedInject
    public DiscardMessageOutput(final Journal journal,
                                final MetricRegistry metricRegistry,
                                @Assisted Stream stream,
                                @Assisted Configuration configuration) {
        this(journal, metricRegistry);
    }

    @Inject
    public DiscardMessageOutput(final Journal journal, final MetricRegistry metricRegistry) {
        this.journal = journal;
        this.messagesDiscarded = metricRegistry.meter(name(this.getClass(), "messagesDiscarded"));
        isRunning.set(true);
    }

    @Override
    public void stop() {
        isRunning.set(false);
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public void write(Message message) throws Exception {
        journal.markJournalOffsetCommitted(message.getJournalOffset());
        messagesDiscarded.mark();
    }

    @Override
    public void write(List<Message> messages) throws Exception {
        long maxOffset = Long.MIN_VALUE;

        for (final Message message : messages) {
            maxOffset = Math.max(message.getJournalOffset(), maxOffset);
        }

        journal.markJournalOffsetCommitted(maxOffset);
        messagesDiscarded.mark(messages.size());
    }

    public interface Factory extends MessageOutput.Factory<DiscardMessageOutput> {
    }

    public static class Config extends MessageOutput.Config {
    }

    public static class Descriptor extends MessageOutput.Descriptor {
        public Descriptor() {
            super("Discard Message output", false, "", "Output that discards messages");
        }
    }
}
