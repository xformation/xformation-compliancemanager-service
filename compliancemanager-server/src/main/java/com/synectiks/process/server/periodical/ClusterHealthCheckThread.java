/*
 * */
package com.synectiks.process.server.periodical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.cluster.NodeNotFoundException;
import com.synectiks.process.server.notifications.Notification;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.plugin.periodical.Periodical;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.shared.inputs.InputRegistry;

import javax.inject.Inject;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public class ClusterHealthCheckThread extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterHealthCheckThread.class);
    private NotificationService notificationService;
    private final InputRegistry inputRegistry;
    private final NodeId nodeId;

    @Inject
    public ClusterHealthCheckThread(NotificationService notificationService,
                                    InputRegistry inputRegistry,
                                    NodeId nodeId) {
        this.notificationService = notificationService;
        this.inputRegistry = inputRegistry;
        this.nodeId = nodeId;
    }

    @Override
    public void doRun() {
        try {
            if (inputRegistry.runningCount() == 0) {
                LOG.debug("No input running in cluster!");
                notificationService.publishIfFirst(getNotification());
            } else {
                LOG.debug("Running inputs found, disabling notification");
                notificationService.fixed(getNotification());
            }
        } catch (NodeNotFoundException e) {
            LOG.error("Unable to find own node: ", e.getMessage(), e);
        }
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    protected Notification getNotification() throws NodeNotFoundException {
        Notification notification = notificationService.buildNow();
        notification.addType(Notification.Type.NO_INPUT_RUNNING);
        notification.addSeverity(Notification.Severity.URGENT);
        notification.addNode(nodeId.toString());

        return notification;
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
        return true;
    }

    @Override
    public int getInitialDelaySeconds() {
        // Wait some time until all inputs have been started otherwise this will trigger a notification on every
        // startup of the server.
        return 120;
    }

    @Override
    public int getPeriodSeconds() {
        return 20;
    }

}
