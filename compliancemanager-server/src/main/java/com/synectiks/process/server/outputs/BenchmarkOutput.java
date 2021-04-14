/*
 * */
package com.synectiks.process.server.outputs;

import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.ImmutableList;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.outputs.MessageOutput;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.shared.journal.Journal;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.codahale.metrics.MetricRegistry.name;

public class BenchmarkOutput implements MessageOutput {
    private static final List<String> SKIPPED_METRIC_PREFIXES = ImmutableList.of("com.synectiks.process.server.rest.resources");

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final Meter messagesWritten;
    private final CsvReporter csvReporter;
    private final Journal journal;

    @AssistedInject
    public BenchmarkOutput(final MetricRegistry metricRegistry,
                           final Journal journal,
                           @Assisted Stream stream,
                           @Assisted Configuration configuration) {
        this(metricRegistry, journal);
    }

    @Inject
    public BenchmarkOutput(final MetricRegistry metricRegistry, final Journal journal) {
        this.journal = journal;
        this.messagesWritten = metricRegistry.meter(name(this.getClass(), "messagesWritten"));

        final File directory = new File("benchmark-csv");
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();

        csvReporter = CsvReporter.forRegistry(metricRegistry)
                .formatFor(Locale.US)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(new CsvMetricFilter(SKIPPED_METRIC_PREFIXES))
                .build(directory);

        csvReporter.start(1, TimeUnit.SECONDS);

        isRunning.set(true);
    }

    @Override
    public void stop() {
        csvReporter.stop();
        isRunning.set(false);
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public void write(Message message) throws Exception {
        journal.markJournalOffsetCommitted(message.getJournalOffset());
        messagesWritten.mark();
    }

    @Override
    public void write(List<Message> messages) throws Exception {
        long maxOffset = Long.MIN_VALUE;

        for (final Message message : messages) {
            maxOffset = Math.max(message.getJournalOffset(), maxOffset);
        }

        journal.markJournalOffsetCommitted(maxOffset);

        messagesWritten.mark(messages.size());
    }

    public interface Factory extends MessageOutput.Factory<BenchmarkOutput> {
        @Override
        BenchmarkOutput create(Stream stream, Configuration configuration);

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    public static class Config extends MessageOutput.Config {
    }

    public static class Descriptor extends MessageOutput.Descriptor {
        public Descriptor() {
            super("Benchmark output", false, "", "Output that benchmarks message rates");
        }
    }

    private static class CsvMetricFilter implements MetricFilter {
        private final List<String> prefixes;

        public CsvMetricFilter(List<String> prefixes) {
            this.prefixes = prefixes;
        }

        @Override
        public boolean matches(String name, Metric metric) {
            for (String prefix : prefixes) {
                if (name.startsWith(prefix)) {
                    return false;
                }
            }

            return true;
        }
    }
}
