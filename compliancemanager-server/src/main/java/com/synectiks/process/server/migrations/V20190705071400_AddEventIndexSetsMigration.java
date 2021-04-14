/*
 * */
package com.synectiks.process.server.migrations;

import com.google.common.collect.ImmutableMap;
import com.mongodb.DuplicateKeyException;
import com.synectiks.process.server.configuration.ElasticsearchConfiguration;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.IndexSetValidator;
import com.synectiks.process.server.indexer.MongoIndexSet;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.indexer.indexset.IndexSetService;
import com.synectiks.process.server.indexer.retention.strategies.DeletionRetentionStrategy;
import com.synectiks.process.server.indexer.retention.strategies.DeletionRetentionStrategyConfig;
import com.synectiks.process.server.indexer.rotation.strategies.TimeBasedRotationStrategy;
import com.synectiks.process.server.indexer.rotation.strategies.TimeBasedRotationStrategyConfig;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.streams.StreamImpl;
import com.synectiks.process.server.streams.StreamService;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.mongojack.DBQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static java.util.Locale.US;
import static java.util.Objects.requireNonNull;

public class V20190705071400_AddEventIndexSetsMigration extends Migration {
    private static final Logger LOG = LoggerFactory.getLogger(V20190705071400_AddEventIndexSetsMigration.class);

    private final ElasticsearchConfiguration elasticsearchConfiguration;
    private final MongoIndexSet.Factory mongoIndexSetFactory;
    private final IndexSetService indexSetService;
    private final IndexSetValidator indexSetValidator;
    private final StreamService streamService;

    @Inject
    public V20190705071400_AddEventIndexSetsMigration(ElasticsearchConfiguration elasticsearchConfiguration,
                                                      MongoIndexSet.Factory mongoIndexSetFactory,
                                                      IndexSetService indexSetService,
                                                      IndexSetValidator indexSetValidator,
                                                      StreamService streamService) {
        this.elasticsearchConfiguration = elasticsearchConfiguration;
        this.mongoIndexSetFactory = mongoIndexSetFactory;
        this.indexSetService = indexSetService;
        this.indexSetValidator = indexSetValidator;
        this.streamService = streamService;
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.parse("2019-07-05T07:14:00Z");
    }

    @Override
    public void upgrade() {
        ensureEventsStreamAndIndexSet(
                "alertmanager Events",
                "Stores alertmanager events.",
                elasticsearchConfiguration.getDefaultEventsIndexPrefix(),
                ElasticsearchConfiguration.DEFAULT_EVENTS_INDEX_PREFIX,
                Stream.DEFAULT_EVENTS_STREAM_ID,
                "All events",
                "Stream containing all events created by alertmanager"
        );
        ensureEventsStreamAndIndexSet(
                "alertmanager System Events",
                "Stores alertmanager system events.",
                elasticsearchConfiguration.getDefaultSystemEventsIndexPrefix(),
                ElasticsearchConfiguration.DEFAULT_SYSTEM_EVENTS_INDEX_PREFIX,
                Stream.DEFAULT_SYSTEM_EVENTS_STREAM_ID,
                "All system events",
                "Stream containing all system events created by alertmanager"
        );
    }

    private void ensureEventsStreamAndIndexSet(String indexSetTitle,
                                               String indexSetDescription,
                                               String indexPrefix,
                                               String indexPrefixConfigKey,
                                               String streamId,
                                               String streamTitle,
                                               String streamDescription) {
        checkIndexPrefixConflicts(indexPrefix, indexPrefixConfigKey);

        final IndexSet eventsIndexSet = setupEventsIndexSet(indexSetTitle, indexSetDescription, indexPrefix);
        try {
            streamService.load(streamId);
        } catch (NotFoundException ignored) {
            createEventsStream(streamId, streamTitle, streamDescription, eventsIndexSet);
        }
    }

    private void checkIndexPrefixConflicts(String indexPrefix, String configKey) {
        final DBQuery.Query query = DBQuery.and(
                DBQuery.notEquals(IndexSetConfig.FIELD_INDEX_TEMPLATE_TYPE, Optional.of(IndexSetConfig.TemplateType.EVENTS)),
                DBQuery.is(IndexSetConfig.FIELD_INDEX_PREFIX, indexPrefix)
        );

        if (indexSetService.findOne(query).isPresent()) {
            final String msg = String.format(US, "Index prefix conflict: a non-events index-set with prefix <%s> already exists. Configure a different <%s> value in the server config file.",
                    indexPrefix, configKey);
            throw new IllegalStateException(msg);
        }
    }

    private Optional<IndexSetConfig> getEventsIndexSetConfig(String indexPrefix) {
        final DBQuery.Query query = DBQuery.and(
                DBQuery.is(IndexSetConfig.FIELD_INDEX_TEMPLATE_TYPE, Optional.of(IndexSetConfig.TemplateType.EVENTS)),
                DBQuery.is(IndexSetConfig.FIELD_INDEX_PREFIX, indexPrefix)
        );
        return indexSetService.findOne(query);
    }

    private IndexSet setupEventsIndexSet(String indexSetTitle, String indexSetDescription, String indexPrefix) {
        final Optional<IndexSetConfig> optionalIndexSetConfig = getEventsIndexSetConfig(indexPrefix);
        if (optionalIndexSetConfig.isPresent()) {
            return mongoIndexSetFactory.create(optionalIndexSetConfig.get());
        }

        final IndexSetConfig indexSetConfig = IndexSetConfig.builder()
                .title(indexSetTitle)
                .description(indexSetDescription)
                .indexTemplateType(IndexSetConfig.TemplateType.EVENTS)
                .isWritable(true)
                .indexPrefix(indexPrefix)
                .shards(elasticsearchConfiguration.getShards())
                .replicas(elasticsearchConfiguration.getReplicas())
                .rotationStrategyClass(TimeBasedRotationStrategy.class.getCanonicalName())
                .rotationStrategy(TimeBasedRotationStrategyConfig.create(Period.months(1)))
                .retentionStrategyClass(DeletionRetentionStrategy.class.getCanonicalName())
                .retentionStrategy(DeletionRetentionStrategyConfig.create(12))
                .creationDate(ZonedDateTime.now(ZoneOffset.UTC))
                .indexAnalyzer(elasticsearchConfiguration.getAnalyzer())
                .indexTemplateName(indexPrefix+ "-template")
                .indexOptimizationMaxNumSegments(elasticsearchConfiguration.getIndexOptimizationMaxNumSegments())
                .indexOptimizationDisabled(elasticsearchConfiguration.isDisableIndexOptimization())
                .fieldTypeRefreshInterval(Duration.standardMinutes(1))
                .build();

        try {
            final Optional<IndexSetValidator.Violation> violation = indexSetValidator.validate(indexSetConfig);
            if (violation.isPresent()) {
                throw new RuntimeException(violation.get().message());
            }

            final IndexSetConfig savedIndexSet = indexSetService.save(indexSetConfig);

            LOG.info("Successfully created events index-set <{}/{}>", savedIndexSet.id(), savedIndexSet.title());

            return mongoIndexSetFactory.create(savedIndexSet);
        } catch (DuplicateKeyException e) {
            LOG.error("Couldn't create index-set <{}/{}>", indexSetTitle, indexPrefix);
            throw new RuntimeException(e.getMessage());
        }
    }

    private void createEventsStream(String streamId, String streamTitle, String streamDescription, IndexSet indexSet) {
        final ObjectId id = new ObjectId(streamId);
        final Map<String, Object> fields = ImmutableMap.<String, Object>builder()
                .put(StreamImpl.FIELD_TITLE, streamTitle)
                .put(StreamImpl.FIELD_DESCRIPTION, streamDescription)
                .put(StreamImpl.FIELD_DISABLED, false)
                .put(StreamImpl.FIELD_CREATED_AT, DateTime.now(DateTimeZone.UTC))
                .put(StreamImpl.FIELD_CREATOR_USER_ID, "admin")
                .put(StreamImpl.FIELD_MATCHING_TYPE, StreamImpl.MatchingType.DEFAULT.name())
                .put(StreamImpl.FIELD_REMOVE_MATCHES_FROM_DEFAULT_STREAM, true)
                .put(StreamImpl.FIELD_INDEX_SET_ID, requireNonNull(indexSet.getConfig().id(), "index set ID cannot be null"))
                .put(StreamImpl.FIELD_DEFAULT_STREAM, false)
                .build();
        final Stream stream = new StreamImpl(id, fields, Collections.emptyList(), Collections.emptySet(), indexSet);

        try {
            streamService.save(stream);
            LOG.info("Successfully created events stream <{}/{}>", stream.getId(), stream.getTitle());
        } catch (ValidationException e) {
            LOG.error("Couldn't create events stream <{}/{}>! This is a bug!", streamId, streamTitle, e);
        }
    }
}
