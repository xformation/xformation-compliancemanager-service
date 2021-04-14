/*
 * */
package com.synectiks.process.server.periodical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.cluster.ClusterId;
import com.synectiks.process.server.plugin.periodical.Periodical;

import javax.inject.Inject;
import java.util.UUID;

public class ClusterIdGeneratorPeriodical extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterIdGeneratorPeriodical.class);

    private final ClusterConfigService clusterConfigService;

    @Inject
    public ClusterIdGeneratorPeriodical(ClusterConfigService clusterConfigService) {
        this.clusterConfigService = clusterConfigService;
    }

    @Override
    public boolean runsForever() {
        return true;
    }

    @Override
    public boolean stopOnGracefulShutdown() {
        return false;
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
    public boolean isDaemon() {
        return true;
    }

    @Override
    public int getInitialDelaySeconds() {
        return 0;
    }

    @Override
    public int getPeriodSeconds() {
        return 0;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public void doRun() {
        if(clusterConfigService.get(ClusterId.class) == null) {
            ClusterId clusterId = ClusterId.create(UUID.randomUUID().toString());
            clusterConfigService.write(clusterId);

            LOG.debug("Generated cluster ID {}", clusterId.clusterId());
        }
    }
}
