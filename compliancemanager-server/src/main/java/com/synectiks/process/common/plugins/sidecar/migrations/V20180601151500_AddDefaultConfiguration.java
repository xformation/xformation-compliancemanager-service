/*
 * */
package com.synectiks.process.common.plugins.sidecar.migrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.common.plugins.sidecar.system.SidecarConfiguration;
import com.synectiks.process.server.migrations.Migration;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;

import javax.inject.Inject;
import java.time.ZonedDateTime;

public class V20180601151500_AddDefaultConfiguration extends Migration {
    private static final Logger LOG = LoggerFactory.getLogger(V20180601151500_AddDefaultConfiguration.class);

     private final ClusterConfigService clusterConfigService;

    @Inject
    public V20180601151500_AddDefaultConfiguration(ClusterConfigService clusterConfigService) {
        this.clusterConfigService = clusterConfigService;
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.parse("2018-06-01T15:15:00Z");
    }

    @Override
    public void upgrade() {
        final SidecarConfiguration sidecarConfiguration = clusterConfigService.get(SidecarConfiguration.class);
        if (sidecarConfiguration == null) {
            final SidecarConfiguration config = SidecarConfiguration.defaultConfiguration();
            LOG.info("Creating Sidecar cluster config: {}", config);
            clusterConfigService.write(config);
        }
    }
}
