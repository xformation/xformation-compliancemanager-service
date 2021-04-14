/*
 * */
package com.synectiks.process.server.periodical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.cluster.Node;
import com.synectiks.process.server.cluster.NodeNotFoundException;
import com.synectiks.process.server.cluster.NodeService;
import com.synectiks.process.server.configuration.HttpConfiguration;
import com.synectiks.process.server.notifications.Notification;
import com.synectiks.process.server.notifications.NotificationImpl;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.periodical.Periodical;
import com.synectiks.process.server.shared.system.activities.Activity;
import com.synectiks.process.server.shared.system.activities.ActivityWriter;

import javax.inject.Inject;

public class NodePingThread extends Periodical {

    private static final Logger LOG = LoggerFactory.getLogger(NodePingThread.class);
    private final NodeService nodeService;
    private final NotificationService notificationService;
    private final ActivityWriter activityWriter;
    private final HttpConfiguration httpConfiguration;
    private final ServerStatus serverStatus;

    @Inject
    public NodePingThread(NodeService nodeService,
                          NotificationService notificationService,
                          ActivityWriter activityWriter,
                          HttpConfiguration httpConfiguration,
                          ServerStatus serverStatus) {
        this.nodeService = nodeService;
        this.notificationService = notificationService;
        this.activityWriter = activityWriter;
        this.httpConfiguration = httpConfiguration;
        this.serverStatus = serverStatus;
    }

    @Override
    public void doRun() {
        final boolean isMaster = serverStatus.hasCapability(ServerStatus.Capability.MASTER);
        try {
            Node node = nodeService.byNodeId(serverStatus.getNodeId());
            nodeService.markAsAlive(node, isMaster, httpConfiguration.getHttpPublishUri().resolve(HttpConfiguration.PATH_API));
        } catch (NodeNotFoundException e) {
            LOG.warn("Did not find meta info of this node. Re-registering.");
            nodeService.registerServer(serverStatus.getNodeId().toString(),
                    isMaster,
                    httpConfiguration.getHttpPublishUri().resolve(HttpConfiguration.PATH_API),
                    Tools.getLocalCanonicalHostname());
        }
        try {
            // Remove old nodes that are no longer running. (Just some housekeeping)
            nodeService.dropOutdated();

            // Check that we still have a master node in the cluster, if not, warn the user.
            if (nodeService.isAnyMasterPresent()) {
                Notification notification = notificationService.build()
                        .addType(Notification.Type.NO_MASTER);
                boolean removedNotification = notificationService.fixed(notification);
                if (removedNotification) {
                    activityWriter.write(
                        new Activity("Notification condition [" + NotificationImpl.Type.NO_MASTER + "] " +
                                             "has been fixed.", NodePingThread.class));
                }
            } else {
                Notification notification = notificationService.buildNow()
                        .addNode(serverStatus.getNodeId().toString())
                        .addType(Notification.Type.NO_MASTER)
                        .addSeverity(Notification.Severity.URGENT);
                notificationService.publishIfFirst(notification);
            }

        } catch (Exception e) {
            LOG.warn("Caught exception during node ping.", e);
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
    public boolean stopOnGracefulShutdown() {
        return false;
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
}
