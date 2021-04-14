/*
 * */
package com.synectiks.process.server.system.stats;

import com.synectiks.process.common.plugins.views.search.views.DashboardService;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfigurationService;
import com.synectiks.process.server.alerts.AlertService;
import com.synectiks.process.server.indexer.cluster.Cluster;
import com.synectiks.process.server.inputs.InputService;
import com.synectiks.process.server.shared.users.UserService;
import com.synectiks.process.server.streams.OutputService;
import com.synectiks.process.server.streams.StreamRuleService;
import com.synectiks.process.server.streams.StreamService;
import com.synectiks.process.server.system.stats.elasticsearch.ElasticsearchStats;
import com.synectiks.process.server.system.stats.mongo.MongoProbe;
import com.synectiks.process.server.system.stats.mongo.MongoStats;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class ClusterStatsService {
    private final MongoProbe mongoProbe;
    private final UserService userService;
    private final InputService inputService;
    private final StreamService streamService;
    private final StreamRuleService streamRuleService;
    private final OutputService outputService;
    private final AlertService alertService;
    private final AlarmCallbackConfigurationService alarmCallbackConfigurationService;
    private final DashboardService dashboardService;
    private final Cluster cluster;

    @Inject
    public ClusterStatsService(MongoProbe mongoProbe,
                               UserService userService,
                               InputService inputService,
                               StreamService streamService,
                               StreamRuleService streamRuleService,
                               OutputService outputService,
                               AlertService alertService,
                               AlarmCallbackConfigurationService alarmCallbackConfigurationService,
                               DashboardService dashboardService,
                               Cluster cluster) {
        this.mongoProbe = mongoProbe;
        this.userService = userService;
        this.inputService = inputService;
        this.streamService = streamService;
        this.streamRuleService = streamRuleService;
        this.outputService = outputService;
        this.alertService = alertService;
        this.alarmCallbackConfigurationService = alarmCallbackConfigurationService;
        this.dashboardService = dashboardService;
        this.cluster = cluster;
    }

    public ClusterStats clusterStats() {
        return ClusterStats.create(
                elasticsearchStats(),
                mongoStats(),
                streamService.count(),
                streamRuleService.totalStreamRuleCount(),
                streamRuleService.streamRuleCountByStream(),
                userService.count(),
                outputService.count(),
                outputService.countByType(),
                countDashboards(),
                inputService.totalCount(),
                inputService.globalCount(),
                inputService.totalCountByType(),
                inputService.totalExtractorCount(),
                inputService.totalExtractorCountByType(),
                alarmStats()
        );
    }

    private long countDashboards() {
        return dashboardService.count();
    }

    public ElasticsearchStats elasticsearchStats() {
        return cluster.elasticsearchStats();
    }

    public MongoStats mongoStats() {
        return mongoProbe.mongoStats();
    }

    public AlarmStats alarmStats() {
        final long totalCount = alertService.totalCount();
        final Map<String, Long> counterPerType = alarmCallbackConfigurationService.countPerType();
        return AlarmStats.create(totalCount, counterPerType);
    }
}
