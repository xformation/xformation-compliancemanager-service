/*
 * */
package com.synectiks.process.common.plugins.sidecar.periodical;

import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.common.plugins.sidecar.services.SidecarService;
import com.synectiks.process.common.plugins.sidecar.system.SidecarConfiguration;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.periodical.Periodical;

import javax.inject.Inject;

public class PurgeExpiredSidecarsThread extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(PurgeExpiredSidecarsThread.class);

    private final SidecarService sidecarService;
    private final SidecarConfiguration sidecarConfiguration;

    @Inject
    public PurgeExpiredSidecarsThread(SidecarService sidecarService,
                                      ClusterConfigService clusterConfigService) {
        this.sidecarService = sidecarService;
        this.sidecarConfiguration = clusterConfigService.getOrDefault(SidecarConfiguration.class, SidecarConfiguration.defaultConfiguration());
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
        return 60 * 10;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public void doRun() {
        final Period inactiveThreshold = this.sidecarConfiguration.sidecarInactiveThreshold();
        final int expiredSidecars = sidecarService.markExpired(inactiveThreshold, "Received no ping signal from sidecar");
        LOG.debug("Marked {} sidecars as inactive.", expiredSidecars);
        final int purgedSidecars = sidecarService.destroyExpired(this.sidecarConfiguration.sidecarExpirationThreshold());
        LOG.debug("Purged {} inactive sidecars.", purgedSidecars);
    }
}
