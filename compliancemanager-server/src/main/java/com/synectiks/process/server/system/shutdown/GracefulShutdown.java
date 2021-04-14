/*
 * */
package com.synectiks.process.server.system.shutdown;

import com.google.common.util.concurrent.Uninterruptibles;
import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.audit.AuditActor;
import com.synectiks.process.server.audit.AuditEventSender;
import com.synectiks.process.server.initializers.BufferSynchronizerService;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.shared.initializers.InputSetupService;
import com.synectiks.process.server.shared.initializers.JerseyService;
import com.synectiks.process.server.shared.initializers.PeriodicalsService;
import com.synectiks.process.server.shared.journal.JournalReader;
import com.synectiks.process.server.shared.system.activities.Activity;
import com.synectiks.process.server.shared.system.activities.ActivityWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.synectiks.process.server.audit.AuditEventTypes.NODE_SHUTDOWN_COMPLETE;

import java.util.concurrent.TimeUnit;

@Singleton
public class GracefulShutdown implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(GracefulShutdown.class);
    private static final int SLEEP_SECS = 1;

    private final Configuration configuration;
    private final BufferSynchronizerService bufferSynchronizerService;
    private final PeriodicalsService periodicalsService;
    private final InputSetupService inputSetupService;
    private final ServerStatus serverStatus;
    private final ActivityWriter activityWriter;
    private final JerseyService jerseyService;
    private final GracefulShutdownService gracefulShutdownService;
    private final AuditEventSender auditEventSender;
    private final JournalReader journalReader;

    @Inject
    public GracefulShutdown(ServerStatus serverStatus,
                            ActivityWriter activityWriter,
                            Configuration configuration,
                            BufferSynchronizerService bufferSynchronizerService,
                            PeriodicalsService periodicalsService,
                            InputSetupService inputSetupService,
                            JerseyService jerseyService,
                            GracefulShutdownService gracefulShutdownService,
                            AuditEventSender auditEventSender,
                            JournalReader journalReader) {
        this.serverStatus = serverStatus;
        this.activityWriter = activityWriter;
        this.configuration = configuration;
        this.bufferSynchronizerService = bufferSynchronizerService;
        this.periodicalsService = periodicalsService;
        this.inputSetupService = inputSetupService;
        this.jerseyService = jerseyService;
        this.gracefulShutdownService = gracefulShutdownService;
        this.auditEventSender = auditEventSender;
        this.journalReader = journalReader;
    }

    @Override
    public void run() {
        doRun(true);
    }

    public void runWithoutExit() {
        doRun(false);
    }

    private void doRun(boolean exit) {
        LOG.info("Graceful shutdown initiated.");
        serverStatus.shutdown();

        // Give possible load balancers time to recognize state change. State is DEAD because of HALTING.
        LOG.info("Node status: [{}]. Waiting <{}sec> for possible load balancers to recognize state change.",
                serverStatus.getLifecycle(),
                configuration.getLoadBalancerRecognitionPeriodSeconds());
        Uninterruptibles.sleepUninterruptibly(configuration.getLoadBalancerRecognitionPeriodSeconds(), TimeUnit.SECONDS);

        activityWriter.write(new Activity("Graceful shutdown initiated.", GracefulShutdown.class));

        /*
         * Wait a second to give for example the calling REST call some time to respond
         * to the client. Using a latch or something here might be a bit over-engineered.
         */
        Uninterruptibles.sleepUninterruptibly(SLEEP_SECS, TimeUnit.SECONDS);

        // Stop REST API service to avoid changes from outside.
        jerseyService.stopAsync();

        // stop all inputs so no new messages can come in
        inputSetupService.stopAsync();

        jerseyService.awaitTerminated();
        inputSetupService.awaitTerminated();

        journalReader.stopAsync().awaitTerminated();

        // Try to flush all remaining messages from the system
        bufferSynchronizerService.stopAsync().awaitTerminated();

        // Stop all services that registered with the shutdown service (e.g. plugins)
        // This must run after the BufferSynchronizerService shutdown to make sure the buffers are empty.
        gracefulShutdownService.stopAsync();

        // stop all maintenance tasks
        periodicalsService.stopAsync().awaitTerminated();

        // Wait until the shutdown service is done
        gracefulShutdownService.awaitTerminated();

        auditEventSender.success(AuditActor.system(serverStatus.getNodeId()), NODE_SHUTDOWN_COMPLETE);

        // Shut down hard with no shutdown hooks running.
        LOG.info("Goodbye.");
        if (exit) {
            System.exit(0);
        }
    }
}
