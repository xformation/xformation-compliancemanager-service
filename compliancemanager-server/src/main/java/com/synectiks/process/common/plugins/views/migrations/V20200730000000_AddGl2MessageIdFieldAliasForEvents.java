/*
 * */
package com.synectiks.process.common.plugins.views.migrations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.configuration.ElasticsearchConfiguration;
import com.synectiks.process.server.migrations.Migration;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.storage.ElasticsearchVersion;

import org.graylog.autovalue.WithBeanGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Set;

public class V20200730000000_AddGl2MessageIdFieldAliasForEvents extends Migration {

    private static final Logger LOG = LoggerFactory.getLogger(V20200730000000_AddGl2MessageIdFieldAliasForEvents.class);

    private static final Version MINIMUM_ELASTICSEARCH_VERSION = Version.from(7, 0, 0);

    private final Version elasticsearchVersion;
    private final ClusterConfigService clusterConfigService;
    private final ElasticsearchAdapter elasticsearch;
    private final ElasticsearchConfiguration elasticsearchConfig;

    @Inject
    public V20200730000000_AddGl2MessageIdFieldAliasForEvents(
            @ElasticsearchVersion Version elasticsearchVersion,
            ClusterConfigService clusterConfigService,
            ElasticsearchAdapter elasticsearch,
            ElasticsearchConfiguration elasticsearchConfig) {
        this.elasticsearchVersion = elasticsearchVersion;
        this.clusterConfigService = clusterConfigService;
        this.elasticsearch = elasticsearch;
        this.elasticsearchConfig = elasticsearchConfig;
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.parse("2020-07-30T00:00:00Z");
    }

    @Override
    public void upgrade() {
        if (shouldSkip()) {
            return;
        }

        final ImmutableSet<String> eventIndexPrefixes = ImmutableSet.of(
                elasticsearchConfig.getDefaultEventsIndexPrefix(),
                elasticsearchConfig.getDefaultSystemEventsIndexPrefix());

        elasticsearch.addXfAlertMessageIdFieldAlias(eventIndexPrefixes);

        writeMigrationCompleted(eventIndexPrefixes);
    }

    private boolean shouldSkip() {
        if (!elasticsearchVersion.sameOrHigher(MINIMUM_ELASTICSEARCH_VERSION)) {
            LOG.debug("Skipping migration, because Elasticsearch major version of {} " +
                            "is lower than the required minimum version of {}.",
                    elasticsearchVersion, MINIMUM_ELASTICSEARCH_VERSION);
            if (hasCompletedBefore()) {
                // This must be a downgrade. We remove the completed marker, so indices created with this lower version
                // would be fixed in case of another upgrade where this migration would then run again.
                clusterConfigService.remove(MigrationCompleted.class);
            }
            return true;
        }
        if (hasCompletedBefore()) {
            LOG.debug("Migration already completed.");
            return true;
        }
        return false;
    }

    private boolean hasCompletedBefore() {
        return clusterConfigService.get(MigrationCompleted.class) != null;
    }

    private void writeMigrationCompleted(ImmutableSet<String> eventIndexPrefixes) {
        this.clusterConfigService.write(V20200730000000_AddGl2MessageIdFieldAliasForEvents.MigrationCompleted.create(eventIndexPrefixes));
    }

    public interface ElasticsearchAdapter {
        void addXfAlertMessageIdFieldAlias(Set<String> indexPrefixes);
    }

    @JsonAutoDetect
    @AutoValue
    @WithBeanGetter
    public static abstract class MigrationCompleted {
        @JsonProperty("modified_index_prefixes")
        public abstract Set<String> modifiedIndexPrefixes();

        @JsonCreator
        public static V20200730000000_AddGl2MessageIdFieldAliasForEvents.MigrationCompleted create(@JsonProperty("modified_index_prefixes") final Set<String> modifiedIndexPrefixes) {
            return new AutoValue_V20200730000000_AddGl2MessageIdFieldAliasForEvents_MigrationCompleted(modifiedIndexPrefixes);
        }
    }
}
