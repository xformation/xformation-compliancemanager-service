/*
 * */
package com.synectiks.process.server.plugin.inputs.transports;

import com.codahale.metrics.MetricSet;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.Service;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.MisfireException;
import com.synectiks.process.server.plugin.inputs.codecs.CodecAggregator;
import com.synectiks.process.server.plugin.journal.RawMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GeneratorTransport extends ThrottleableTransport {
    private static final Logger log = LoggerFactory.getLogger(GeneratorTransport.class);

    private Service generatorService;

    public GeneratorTransport(EventBus eventBus, Configuration configuration) {
        super(eventBus, configuration);
    }

    @Override
    public void setMessageAggregator(CodecAggregator aggregator) {

    }

    protected abstract RawMessage produceRawMessage(MessageInput input);

    @Override
    public void doLaunch(final MessageInput input) throws MisfireException {
        generatorService = new AbstractExecutionThreadService() {
            Thread runThread;

            @Override
            protected void run() throws Exception {
                while (isRunning()) {

                    if (isThrottled()) {
                        blockUntilUnthrottled();
                    }
                    final RawMessage rawMessage = GeneratorTransport.this.produceRawMessage(input);
                    if (rawMessage != null) {
                        input.processRawMessage(rawMessage);
                    }
                }
            }

            @Override
            protected void startUp() throws Exception {
                runThread = Thread.currentThread();
            }

            @Override
            protected void triggerShutdown() {
                runThread.interrupt();
            }
        };

        generatorService.startAsync();
    }

    @Override
    public void doStop() {
        if (generatorService == null || !generatorService.isRunning()) {
            log.error("Cannot stop generator transport, it isn't running.");
            return;
        }
        log.debug("Stopping generator transport service {}", generatorService);
        generatorService.stopAsync().awaitTerminated();
        generatorService = null;
    }

    @Override
    public MetricSet getMetricSet() {
        return null;
    }

    public static class Config extends ThrottleableTransport.Config {
    }
}
