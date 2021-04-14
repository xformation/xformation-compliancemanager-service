/*
 * */
package com.synectiks.process.server.periodical;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.github.joschi.jadconfig.util.Size;
import com.synectiks.process.server.plugin.GlobalMetricNames;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.periodical.Periodical;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.system.traffic.TrafficCounterService;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class TrafficCounterCalculator extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(TrafficCounterCalculator.class);
    private final NodeId nodeId;
    private final TrafficCounterService trafficService;
    private final MetricRegistry metricRegistry;
    private long previousInputBytes = 0L;
    private long previousOutputBytes = 0L;
    private long previousDecodedBytes = 0L;
    private Counter inputCounter;
    private Counter outputCounter;
    private Counter decodedCounter;

    @Inject
    public TrafficCounterCalculator(NodeId nodeId, TrafficCounterService trafficService, MetricRegistry metricRegistry) {
        this.nodeId = nodeId;
        this.trafficService = trafficService;
        this.metricRegistry = metricRegistry;
    }

    @Override
    public void initialize() {
        inputCounter = metricRegistry.counter(GlobalMetricNames.INPUT_TRAFFIC);
        outputCounter = metricRegistry.counter(GlobalMetricNames.OUTPUT_TRAFFIC);
        decodedCounter = metricRegistry.counter(GlobalMetricNames.DECODED_TRAFFIC);
    }

    @Override
    public void doRun() {
        final DateTime now = Tools.nowUTC();
        final int secondOfMinute = now.getSecondOfMinute();
        // on the top of every minute, we flush the current throughput
        if (secondOfMinute == 0) {
            LOG.trace("Calculating input and output traffic for the previous minute");

            final long currentInputBytes = inputCounter.getCount();
            final long currentOutputBytes = outputCounter.getCount();
            final long currentDecodedBytes = decodedCounter.getCount();

            final long inputLastMinute = currentInputBytes - previousInputBytes;
            previousInputBytes = currentInputBytes;
            final long outputBytesLastMinute = currentOutputBytes - previousOutputBytes;
            previousOutputBytes = currentOutputBytes;
            final long decodedBytesLastMinute = currentDecodedBytes - previousDecodedBytes;
            previousDecodedBytes = currentDecodedBytes;

            if (LOG.isDebugEnabled()) {
                final Size in = Size.bytes(inputLastMinute);
                final Size out = Size.bytes(outputBytesLastMinute);
                final Size decoded = Size.bytes(decodedBytesLastMinute);
                LOG.debug("Traffic in the last minute: in: {} bytes ({} MB), out: {} bytes ({} MB}), decoded: {} bytes ({} MB})",
                        in, in.toMegabytes(), out, out.toMegabytes(), decoded, decoded.toMegabytes());
            }
            final DateTime previousMinute = now.minusMinutes(1);
            trafficService.updateTraffic(previousMinute, nodeId, inputLastMinute, outputBytesLastMinute, decodedBytesLastMinute);
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
        return false;
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
        return 0;
    }

    @Override
    public int getPeriodSeconds() {
        return 1;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
