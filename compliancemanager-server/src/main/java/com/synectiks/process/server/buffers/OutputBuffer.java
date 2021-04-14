/*
 * */
package com.synectiks.process.server.buffers;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.InstrumentedThreadFactory;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.synectiks.process.server.buffers.processors.OutputBufferProcessor;
import com.synectiks.process.server.plugin.GlobalMetricNames;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.buffers.Buffer;
import com.synectiks.process.server.plugin.buffers.MessageEvent;
import com.synectiks.process.server.shared.buffers.LoggingExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.concurrent.ThreadFactory;

import static com.codahale.metrics.MetricRegistry.name;
import static com.synectiks.process.server.shared.metrics.MetricUtils.constantGauge;
import static com.synectiks.process.server.shared.metrics.MetricUtils.safelyRegister;

@Singleton
public class OutputBuffer extends Buffer {
    private static final Logger LOG = LoggerFactory.getLogger(OutputBuffer.class);

    private final Meter incomingMessages;

    @Inject
    public OutputBuffer(MetricRegistry metricRegistry,
                        Provider<OutputBufferProcessor> processorProvider,
                        @Named("outputbuffer_processors") int processorCount,
                        @Named("ring_size") int ringSize,
                        @Named("processor_wait_strategy") String waitStrategyName) {
        this.ringBufferSize = ringSize;
        this.incomingMessages = metricRegistry.meter(name(OutputBuffer.class, "incomingMessages"));

        safelyRegister(metricRegistry, GlobalMetricNames.OUTPUT_BUFFER_USAGE, new Gauge<Long>() {
            @Override
            public Long getValue() {
                return OutputBuffer.this.getUsage();
            }
        });
        safelyRegister(metricRegistry, GlobalMetricNames.OUTPUT_BUFFER_SIZE, constantGauge(ringBufferSize));

        final ThreadFactory threadFactory = threadFactory(metricRegistry);
        final WaitStrategy waitStrategy = getWaitStrategy(waitStrategyName, "processor_wait_strategy");
        final Disruptor<MessageEvent> disruptor = new Disruptor<>(
                MessageEvent.EVENT_FACTORY,
                this.ringBufferSize,
                threadFactory,
                ProducerType.MULTI,
                waitStrategy
        );
        disruptor.setDefaultExceptionHandler(new LoggingExceptionHandler(LOG));

        LOG.info("Initialized OutputBuffer with ring size <{}> and wait strategy <{}>.",
                ringBufferSize, waitStrategy.getClass().getSimpleName());

        final OutputBufferProcessor[] processors = new OutputBufferProcessor[processorCount];

        for (int i = 0; i < processorCount; i++) {
            processors[i] = processorProvider.get();
        }

        disruptor.handleEventsWithWorkerPool(processors);

        ringBuffer = disruptor.start();
    }

    private ThreadFactory threadFactory(final MetricRegistry metricRegistry) {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("outputbufferprocessor-%d").build();
        return new InstrumentedThreadFactory(threadFactory, metricRegistry, name(this.getClass(), "thread-factory"));
    }

    public void insertBlocking(Message message) {
        insert(message);
    }

    @Override
    protected void afterInsert(int n) {
        incomingMessages.mark(n);
    }
}
