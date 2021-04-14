/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.db;

import com.synectiks.process.common.plugins.pipelineprocessor.events.RuleMetricsConfigChangedEvent;
import com.synectiks.process.server.events.ClusterEventBus;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;

import javax.inject.Inject;

public class RuleMetricsConfigService {
    private final ClusterConfigService clusterConfigService;
    private final ClusterEventBus clusterEventBus;

    @Inject
    public RuleMetricsConfigService(ClusterConfigService clusterConfigService,
                                    ClusterEventBus clusterEventBus) {
        this.clusterConfigService = clusterConfigService;
        this.clusterEventBus = clusterEventBus;
    }

    public RuleMetricsConfigDto save(RuleMetricsConfigDto config) {
        clusterConfigService.write(config);
        clusterEventBus.post(RuleMetricsConfigChangedEvent.create(config.metricsEnabled()));
        return get();
    }

    public RuleMetricsConfigDto get() {
        return clusterConfigService.getOrDefault(RuleMetricsConfigDto.class, RuleMetricsConfigDto.createDefault());
    }
}
