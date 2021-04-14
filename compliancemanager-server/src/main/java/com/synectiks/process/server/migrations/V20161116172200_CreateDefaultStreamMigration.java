/*
 * */
package com.synectiks.process.server.migrations;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.indexer.IndexSet;
import com.synectiks.process.server.indexer.IndexSetRegistry;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.streams.StreamImpl;
import com.synectiks.process.server.streams.StreamService;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * Migration creating the default stream if it doesn't exist.
 */
public class V20161116172200_CreateDefaultStreamMigration extends Migration {
    private static final Logger LOG = LoggerFactory.getLogger(V20161116172200_CreateDefaultStreamMigration.class);

    private final StreamService streamService;
    private final IndexSetRegistry indexSetRegistry;

    @Inject
    public V20161116172200_CreateDefaultStreamMigration(StreamService streamService,
                                                        IndexSetRegistry indexSetRegistry) {
        this.streamService = streamService;
        this.indexSetRegistry = indexSetRegistry;
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.parse("2016-11-16T17:22:00Z");
    }

    @Override
    public void upgrade() {
        try {
            streamService.load(Stream.DEFAULT_STREAM_ID);
        } catch (NotFoundException ignored) {
            createDefaultStream();
        }
    }

    private void createDefaultStream() {
        final IndexSet indexSet = indexSetRegistry.getDefault();

        final ObjectId id = new ObjectId(Stream.DEFAULT_STREAM_ID);
        final Map<String, Object> fields = ImmutableMap.<String, Object>builder()
                .put(StreamImpl.FIELD_TITLE, "All messages")
                .put(StreamImpl.FIELD_DESCRIPTION, "Stream containing all messages")
                .put(StreamImpl.FIELD_DISABLED, false)
                .put(StreamImpl.FIELD_CREATED_AT, DateTime.now(DateTimeZone.UTC))
                .put(StreamImpl.FIELD_CREATOR_USER_ID, "local:admin")
                .put(StreamImpl.FIELD_MATCHING_TYPE, StreamImpl.MatchingType.DEFAULT.name())
                .put(StreamImpl.FIELD_REMOVE_MATCHES_FROM_DEFAULT_STREAM, false)
                .put(StreamImpl.FIELD_DEFAULT_STREAM, true)
                .put(StreamImpl.FIELD_INDEX_SET_ID, indexSet.getConfig().id())
                .build();
        final Stream stream = new StreamImpl(id, fields, Collections.emptyList(), Collections.emptySet(), indexSet);

        try {
            streamService.save(stream);
            LOG.info("Successfully created default stream: {}", stream.getTitle());
        } catch (ValidationException e) {
            LOG.error("Couldn't create default stream! This is a bug!");
        }
    }
}
