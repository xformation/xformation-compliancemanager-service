/*
 * */
package com.synectiks.process.server.migrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.configuration.ElasticsearchConfiguration;
import com.synectiks.process.server.indexer.indexset.DefaultIndexSetConfig;
import com.synectiks.process.server.indexer.indexset.DefaultIndexSetCreated;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indexset.IndexSetService;
import com.synectiks.process.server.indexer.management.IndexManagementConfig;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.indexer.retention.RetentionStrategy;
import com.synectiks.process.server.plugin.indexer.retention.RetentionStrategyConfig;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategy;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategyConfig;

import javax.inject.Inject;
import javax.inject.Provider;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

/**
 * Migration creating the default index set from the legacy settings.
 */
public class V20161116172100_DefaultIndexSetMigration extends Migration {
    private static final Logger LOG = LoggerFactory.getLogger(V20161116172100_DefaultIndexSetMigration.class);

    private final ElasticsearchConfiguration elasticsearchConfiguration;
    private final Map<String, Provider<RotationStrategy>> rotationStrategies;
    private final Map<String, Provider<RetentionStrategy>> retentionStrategies;
    private final IndexSetService indexSetService;
    private final ClusterConfigService clusterConfigService;

    @Inject
    public V20161116172100_DefaultIndexSetMigration(final ElasticsearchConfiguration elasticsearchConfiguration,
                                                    final Map<String, Provider<RotationStrategy>> rotationStrategies,
                                                    final Map<String, Provider<RetentionStrategy>> retentionStrategies,
                                                    final IndexSetService indexSetService,
                                                    final ClusterConfigService clusterConfigService) {
        this.elasticsearchConfiguration = elasticsearchConfiguration;
        this.rotationStrategies = requireNonNull(rotationStrategies);
        this.retentionStrategies = requireNonNull(retentionStrategies);
        this.indexSetService = indexSetService;
        this.clusterConfigService = clusterConfigService;
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.parse("2016-11-16T17:21:00Z");
    }

    @Override
    public void upgrade() {
        // Do not run again if the migration marker can be found in the database.
        if (clusterConfigService.get(DefaultIndexSetCreated.class) != null) {
            return;
        }

        final IndexManagementConfig indexManagementConfig = clusterConfigService.get(IndexManagementConfig.class);

        checkState(indexManagementConfig != null, "Couldn't find index management configuration");

        final IndexSetConfig config = IndexSetConfig.builder()
                .title("Default index set")
                .description("The alertmanager default index set")
                .indexPrefix(elasticsearchConfiguration.getIndexPrefix())
                .shards(elasticsearchConfiguration.getShards())
                .replicas(elasticsearchConfiguration.getReplicas())
                .rotationStrategy(getRotationStrategyConfig(indexManagementConfig))
                .retentionStrategy(getRetentionStrategyConfig(indexManagementConfig))
                .creationDate(ZonedDateTime.now(ZoneOffset.UTC))
                .indexAnalyzer(elasticsearchConfiguration.getAnalyzer())
                .indexTemplateName(elasticsearchConfiguration.getTemplateName())
                .indexOptimizationMaxNumSegments(elasticsearchConfiguration.getIndexOptimizationMaxNumSegments())
                .indexOptimizationDisabled(elasticsearchConfiguration.isDisableIndexOptimization())
                .build();

        final IndexSetConfig savedConfig = indexSetService.save(config);
        clusterConfigService.write(DefaultIndexSetConfig.create(savedConfig.id()));
        clusterConfigService.write(DefaultIndexSetCreated.create());

        LOG.debug("Successfully created default index set: {}", savedConfig);
    }

    private RotationStrategyConfig getRotationStrategyConfig(IndexManagementConfig indexManagementConfig) {
        final String strategyName = indexManagementConfig.rotationStrategy();
        final Provider<RotationStrategy> provider = rotationStrategies.get(strategyName);
        checkState(provider != null, "Couldn't retrieve rotation strategy provider for <" + strategyName + ">");

        final RotationStrategy rotationStrategy = provider.get();
        @SuppressWarnings("unchecked")
        final Class<RotationStrategyConfig> configClass = (Class<RotationStrategyConfig>) rotationStrategy.configurationClass();

        final RotationStrategyConfig rotationStrategyConfig = clusterConfigService.get(configClass);
        checkState(rotationStrategyConfig != null, "Couldn't retrieve rotation strategy config for <" + strategyName + ">");

        return rotationStrategyConfig;
    }

    private RetentionStrategyConfig getRetentionStrategyConfig(IndexManagementConfig indexManagementConfig) {
        final String strategyName = indexManagementConfig.retentionStrategy();
        final Provider<RetentionStrategy> provider = retentionStrategies.get(strategyName);
        checkState(provider != null, "Couldn't retrieve retention strategy provider for <" + strategyName + ">");

        final RetentionStrategy retentionStrategy = provider.get();
        @SuppressWarnings("unchecked")
        final Class<RetentionStrategyConfig> configClass = (Class<RetentionStrategyConfig>) retentionStrategy.configurationClass();

        final RetentionStrategyConfig retentionStrategyConfig = clusterConfigService.get(configClass);
        checkState(retentionStrategyConfig!= null, "Couldn't retrieve retention strategy config for <" + strategyName + ">");

        return retentionStrategyConfig;
    }
}
