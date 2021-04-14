/*
 * */
package com.synectiks.process.server.migrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.configuration.ElasticsearchConfiguration;
import com.synectiks.process.server.indexer.indexset.DefaultIndexSetCreated;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indexset.IndexSetService;
import com.synectiks.process.server.indexer.indexset.V20161216123500_Succeeded;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;

import javax.inject.Inject;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;

/**
 * Migration for moving indexing settings into existing index sets.
 */
public class V20161216123500_DefaultIndexSetMigration extends Migration {
    private static final Logger LOG = LoggerFactory.getLogger(V20161216123500_DefaultIndexSetMigration.class);

    private final ElasticsearchConfiguration elasticsearchConfiguration;
    private final IndexSetService indexSetService;
    private final ClusterConfigService clusterConfigService;

    @Inject
    public V20161216123500_DefaultIndexSetMigration(final ElasticsearchConfiguration elasticsearchConfiguration,
                                                    final IndexSetService indexSetService,
                                                    final ClusterConfigService clusterConfigService) {
        this.elasticsearchConfiguration = elasticsearchConfiguration;
        this.indexSetService = indexSetService;
        this.clusterConfigService = clusterConfigService;
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.of(2016, 12, 16, 12, 35, 0, 0, ZoneOffset.UTC);
    }

    @Override
    public void upgrade() {
        if (clusterConfigService.get(V20161216123500_Succeeded.class) != null) {
            return;
        }

        // The default index set must have been created first.
        checkState(clusterConfigService.get(DefaultIndexSetCreated.class) != null, "The default index set hasn't been created yet. This is a bug!");

        final IndexSetConfig defaultIndexSet= indexSetService.getDefault();
        migrateIndexSet(defaultIndexSet, elasticsearchConfiguration.getTemplateName());

        final List<IndexSetConfig> allWithoutDefault = indexSetService.findAll()
                .stream()
                .filter(indexSetConfig -> !indexSetConfig.equals(defaultIndexSet))
                .collect(Collectors.toList());

        for (IndexSetConfig indexSetConfig : allWithoutDefault) {
            migrateIndexSet(indexSetConfig, indexSetConfig.indexPrefix() + "-template");
        }


        clusterConfigService.write(V20161216123500_Succeeded.create());
    }

    private void migrateIndexSet(IndexSetConfig indexSetConfig, String templateName) {
        final String analyzer = elasticsearchConfiguration.getAnalyzer();
        final IndexSetConfig updatedConfig = indexSetConfig.toBuilder()
                .indexAnalyzer(analyzer)
                .indexTemplateName(templateName)
                .indexOptimizationMaxNumSegments(elasticsearchConfiguration.getIndexOptimizationMaxNumSegments())
                .indexOptimizationDisabled(elasticsearchConfiguration.isDisableIndexOptimization())
                .build();

        final IndexSetConfig savedConfig = indexSetService.save(updatedConfig);

        LOG.debug("Successfully updated index set: {}", savedConfig);
    }
}
