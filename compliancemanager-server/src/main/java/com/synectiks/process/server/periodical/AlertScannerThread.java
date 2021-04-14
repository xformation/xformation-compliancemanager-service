/*
 * */
package com.synectiks.process.server.periodical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.alerts.AlertScanner;
import com.synectiks.process.server.plugin.periodical.Periodical;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.streams.StreamService;

import javax.inject.Inject;
import java.util.List;

public class AlertScannerThread extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(AlertScannerThread.class);

    private final StreamService streamService;
    private final Configuration configuration;
    private final AlertScanner alertScanner;

    @Inject
    public AlertScannerThread(final StreamService streamService,
                              final Configuration configuration,
                              final AlertScanner alertScanner) {
        this.streamService = streamService;
        this.configuration = configuration;
        this.alertScanner = alertScanner;
    }

    @Override
    public void doRun() {
        LOG.debug("Running alert checks.");
        final List<Stream> alertedStreams = streamService.loadAllWithConfiguredAlertConditions();

        LOG.debug("There are {} streams with configured alert conditions.", alertedStreams.size());

        // Load all streams that have configured alert conditions.
        for (Stream stream : alertedStreams) {
            LOG.debug("Stream [{}] has [{}] configured alert conditions.", stream, streamService.getAlertConditions(stream).size());

            if(stream.isPaused()) {
                LOG.debug("Stream [{}] has been paused. Skipping alert check.", stream);
                continue;
            }

            // Check if a threshold is reached.
            streamService.getAlertConditions(stream).forEach(alertCondition -> alertScanner.checkAlertCondition(stream, alertCondition));
        }
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public boolean runsForever() {
        return false;
    }

    @Override
    public boolean isDaemon() {
        return true;
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
        return configuration.isEnableLegacyAlerts();
    }

    @Override
    public int getInitialDelaySeconds() {
        return 10;
    }

    @Override
    public int getPeriodSeconds() {
        return configuration.getAlertCheckInterval();
    }
}
