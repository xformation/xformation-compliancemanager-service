/*
 * */
package com.synectiks.process.common.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.cluster.NodeNotFoundException;
import com.synectiks.process.server.cluster.NodeService;
import com.synectiks.process.server.plugin.system.NodeId;

import javax.inject.Inject;

/**
 * This is the default {@link JobSchedulerConfig}.
 */
public class DefaultJobSchedulerConfig implements JobSchedulerConfig {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultJobSchedulerConfig.class);

    private final NodeService nodeService;
    private final NodeId nodeId;

    @Inject
    public DefaultJobSchedulerConfig(NodeService nodeService, NodeId nodeId) {
        this.nodeService = nodeService;
        this.nodeId = nodeId;
    }

    @Override
    public boolean canStart() {
        try {
            return nodeService.byNodeId(nodeId).isMaster();
        } catch (NodeNotFoundException e) {
            LOG.error("Couldn't find current node <{}> in the database", nodeId.toString(), e);
            return false;
        }
    }

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public int numberOfWorkerThreads() {
        return 5;
    }
}
