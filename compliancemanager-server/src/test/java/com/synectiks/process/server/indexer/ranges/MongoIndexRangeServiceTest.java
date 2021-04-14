/*
 * */
package com.synectiks.process.server.indexer.ranges;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.audit.NullAuditEventSender;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.indexer.ElasticsearchException;
import com.synectiks.process.server.indexer.IndexSetRegistry;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.indexer.indices.events.IndicesClosedEvent;
import com.synectiks.process.server.indexer.indices.events.IndicesDeletedEvent;
import com.synectiks.process.server.indexer.indices.events.IndicesReopenedEvent;
import com.synectiks.process.server.indexer.ranges.IndexRange;
import com.synectiks.process.server.indexer.ranges.MongoIndexRange;
import com.synectiks.process.server.indexer.ranges.MongoIndexRangeService;
import com.synectiks.process.server.indexer.searches.IndexRangeStats;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.assertj.jodatime.api.Assertions;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MongoIndexRangeServiceTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();
    private final MongoJackObjectMapperProvider objectMapperProvider = new MongoJackObjectMapperProvider(objectMapper);

    @Mock
    private Indices indices;
    @Mock
    private IndexSetRegistry indexSetRegistry;
    private EventBus localEventBus;
    private MongoIndexRangeService indexRangeService;

    @Before
    public void setUp() throws Exception {
        localEventBus = new EventBus("local-event-bus");
        indexRangeService = new MongoIndexRangeService(mongodb.mongoConnection(), objectMapperProvider, indices, indexSetRegistry, new NullAuditEventSender(), mock(NodeId.class), localEventBus);
    }

    @Test
    @MongoDBFixtures("MongoIndexRangeServiceTest.json")
    public void getReturnsExistingIndexRange() throws Exception {
        IndexRange indexRange = indexRangeService.get("graylog_1");

        assertThat(indexRange.indexName()).isEqualTo("graylog_1");
        assertThat(indexRange.begin()).isEqualTo(new DateTime(2015, 1, 1, 0, 0, DateTimeZone.UTC));
        assertThat(indexRange.end()).isEqualTo(new DateTime(2015, 1, 2, 0, 0, DateTimeZone.UTC));
        assertThat(indexRange.calculatedAt()).isEqualTo(new DateTime(2015, 1, 2, 0, 0, DateTimeZone.UTC));
        assertThat(indexRange.calculationDuration()).isEqualTo(23);
    }

    @Test(expected = NotFoundException.class)
    @MongoDBFixtures("MongoIndexRangeServiceTest-LegacyIndexRanges.json")
    public void getIgnoresLegacyIndexRange() throws Exception {
        indexRangeService.get("graylog_0");
    }

    @Test(expected = NotFoundException.class)
    public void getThrowsNotFoundException() throws Exception {
        indexRangeService.get("does-not-exist");
    }

    /**
     * Test the following constellation:
     * <pre>
     *                        [-        index range       -]
     * [- graylog_1 -][- graylog_2 -][- graylog_3 -][- graylog_4 -][- graylog_5 -]
     * </pre>
     */
    @Test
    @MongoDBFixtures("MongoIndexRangeServiceTest-distinct.json")
    public void findReturnsIndexRangesWithinGivenRange() throws Exception {
        final DateTime begin = new DateTime(2015, 1, 2, 12, 0, DateTimeZone.UTC);
        final DateTime end = new DateTime(2015, 1, 4, 12, 0, DateTimeZone.UTC);
        final SortedSet<IndexRange> indexRanges = indexRangeService.find(begin, end);

        assertThat(indexRanges).containsExactly(
                MongoIndexRange.create(new ObjectId("55e0261a0cc6980000000002"), "graylog_2", new DateTime(2015, 1, 2, 0, 0, DateTimeZone.UTC), new DateTime(2015, 1, 3, 0, 0, DateTimeZone.UTC), new DateTime(2015, 1, 3, 0, 0, DateTimeZone.UTC), 42),
                MongoIndexRange.create(new ObjectId("55e0261a0cc6980000000003"), "graylog_3", new DateTime(2015, 1, 3, 0, 0, DateTimeZone.UTC), new DateTime(2015, 1, 4, 0, 0, DateTimeZone.UTC), new DateTime(2015, 1, 4, 0, 0, DateTimeZone.UTC), 42),
                MongoIndexRange.create(new ObjectId("55e0261a0cc6980000000004"), "graylog_4", new DateTime(2015, 1, 4, 0, 0, DateTimeZone.UTC), new DateTime(2015, 1, 5, 0, 0, DateTimeZone.UTC), new DateTime(2015, 1, 5, 0, 0, DateTimeZone.UTC), 42)
        );
    }

    @Test
    @MongoDBFixtures("MongoIndexRangeServiceTest-LegacyIndexRanges.json")
    public void findIgnoresLegacyIndexRanges() throws Exception {
        final DateTime begin = new DateTime(2015, 1, 1, 0, 0, DateTimeZone.UTC);
        final DateTime end = new DateTime(2015, 2, 1, 0, 0, DateTimeZone.UTC);
        final SortedSet<IndexRange> indexRanges = indexRangeService.find(begin, end);

        assertThat(indexRanges).containsOnly(
                MongoIndexRange.create(new ObjectId("55e0261a0cc6980000000003"), "graylog_1", new DateTime(2015, 1, 1, 0, 0, DateTimeZone.UTC), new DateTime(2015, 1, 2, 0, 0, DateTimeZone.UTC), new DateTime(2015, 1, 2, 0, 0, DateTimeZone.UTC), 42)
        );
    }

    @Test
    @MongoDBFixtures("MongoIndexRangeServiceTest.json")
    public void findReturnsNothingBeforeBegin() throws Exception {
        final DateTime begin = new DateTime(2016, 1, 1, 0, 0, DateTimeZone.UTC);
        final DateTime end = new DateTime(2016, 1, 2, 0, 0, DateTimeZone.UTC);
        Set<IndexRange> indexRanges = indexRangeService.find(begin, end);

        assertThat(indexRanges).isEmpty();
    }

    @Test
    @MongoDBFixtures("MongoIndexRangeServiceTest.json")
    public void findAllReturnsAllIndexRanges() throws Exception {
        assertThat(indexRangeService.findAll()).hasSize(2);
    }

    @Test
    @MongoDBFixtures("MongoIndexRangeServiceTest-LegacyIndexRanges.json")
    public void findAllReturnsAllIgnoresLegacyIndexRanges() throws Exception {
        assertThat(indexRangeService.findAll()).hasSize(1);
    }

    @Test
    @MongoDBFixtures("MongoIndexRangeServiceTest.json")
    public void calculateRangeReturnsIndexRange() throws Exception {
        final String index = "graylog";
        final DateTime min = new DateTime(2015, 1, 1, 1, 0, DateTimeZone.UTC);
        final DateTime max = new DateTime(2015, 1, 1, 5, 0, DateTimeZone.UTC);
        when(indices.indexRangeStatsOfIndex(index)).thenReturn(IndexRangeStats.create(min, max));

        final IndexRange indexRange = indexRangeService.calculateRange(index);

        assertThat(indexRange.indexName()).isEqualTo(index);
        assertThat(indexRange.begin()).isEqualTo(min);
        assertThat(indexRange.end()).isEqualTo(max);
        Assertions.assertThat(indexRange.calculatedAt()).isEqualToIgnoringHours(DateTime.now(DateTimeZone.UTC));
    }

    @Test(expected = ElasticsearchException.class)
    public void calculateRangeFailsIfIndexIsNotHealthy() throws Exception {
        final String index = "graylog";
        when(indices.waitForRecovery(index)).thenThrow(new ElasticsearchException("TEST"));

        indexRangeService.calculateRange(index);
    }

    @Test
    @MongoDBFixtures("MongoIndexRangeServiceTest-EmptyCollection.json")
    public void testCalculateRangeWithEmptyIndex() throws Exception {
        final String index = "graylog";
        when(indices.indexRangeStatsOfIndex(index)).thenReturn(IndexRangeStats.EMPTY);

        final IndexRange range = indexRangeService.calculateRange(index);

        assertThat(range).isNotNull();
        assertThat(range.indexName()).isEqualTo(index);
        assertThat(range.begin()).isEqualTo(new DateTime(0L, DateTimeZone.UTC));
        assertThat(range.end()).isEqualTo(new DateTime(0L, DateTimeZone.UTC));
    }

    @Test(expected = ElasticsearchException.class)
    public void testCalculateRangeWithNonExistingIndex() throws Exception {
        when(indices.indexRangeStatsOfIndex("does-not-exist")).thenThrow(new ElasticsearchException("does-not-exist"));
        indexRangeService.calculateRange("does-not-exist");
    }

    @Test
    public void savePersistsIndexRange() throws Exception {
        final String indexName = "graylog";
        final DateTime begin = new DateTime(2015, 1, 1, 0, 0, DateTimeZone.UTC);
        final DateTime end = new DateTime(2015, 1, 2, 0, 0, DateTimeZone.UTC);
        final DateTime now = DateTime.now(DateTimeZone.UTC);
        final IndexRange indexRange = MongoIndexRange.create(indexName, begin, end, now, 42);

        indexRangeService.save(indexRange);

        final IndexRange result = indexRangeService.get(indexName);
        assertThat(result.indexName()).isEqualTo(indexName);
        assertThat(result.begin()).isEqualTo(begin);
        assertThat(result.end()).isEqualTo(end);
        assertThat(result.calculatedAt()).isEqualTo(now);
        assertThat(result.calculationDuration()).isEqualTo(42);
    }

    @Test
    public void saveOverwritesExistingIndexRange() throws Exception {
        final String indexName = "graylog";
        final DateTime begin = new DateTime(2015, 1, 1, 0, 0, DateTimeZone.UTC);
        final DateTime end = new DateTime(2015, 1, 2, 0, 0, DateTimeZone.UTC);
        final DateTime now = DateTime.now(DateTimeZone.UTC);
        final IndexRange indexRangeBefore = MongoIndexRange.create(indexName, begin, end, now, 1);
        final IndexRange indexRangeAfter = MongoIndexRange.create(indexName, begin, end, now, 2);

        indexRangeService.save(indexRangeBefore);

        final IndexRange before = indexRangeService.get(indexName);
        assertThat(before.calculationDuration()).isEqualTo(1);

        indexRangeService.save(indexRangeAfter);

        final IndexRange after = indexRangeService.get(indexName);
        assertThat(after.calculationDuration()).isEqualTo(2);
    }

    @Test
    @MongoDBFixtures("MongoIndexRangeServiceTest.json")
    public void remove() throws Exception {
        assertThat(indexRangeService.findAll()).hasSize(2);

        assertThat(indexRangeService.remove("graylog_1")).isTrue();
        assertThat(indexRangeService.remove("graylog_1")).isFalse();

        assertThat(indexRangeService.findAll()).hasSize(1);
    }

    @Test
    @MongoDBFixtures("MongoIndexRangeServiceTest.json")
    public void testHandleIndexDeletion() throws Exception {
        when(indexSetRegistry.isManagedIndex("graylog_1")).thenReturn(true);

        assertThat(indexRangeService.findAll()).hasSize(2);

        localEventBus.post(IndicesDeletedEvent.create(Collections.singleton("graylog_1")));

        assertThat(indexRangeService.findAll()).hasSize(1);
    }

    @Test
    @MongoDBFixtures("MongoIndexRangeServiceTest.json")
    public void testHandleIndexClosing() throws Exception {
        when(indexSetRegistry.isManagedIndex("graylog_1")).thenReturn(true);

        assertThat(indexRangeService.findAll()).hasSize(2);

        localEventBus.post(IndicesClosedEvent.create(Collections.singleton("graylog_1")));

        assertThat(indexRangeService.findAll()).hasSize(1);
    }

    @Test
    @MongoDBFixtures("MongoIndexRangeServiceTest.json")
    public void testHandleIndexReopening() throws Exception {
        final DateTime begin = new DateTime(2016, 1, 1, 0, 0, DateTimeZone.UTC);
        final DateTime end = new DateTime(2016, 1, 15, 0, 0, DateTimeZone.UTC);
        when(indices.indexRangeStatsOfIndex("graylog_3")).thenReturn(IndexRangeStats.create(begin, end));
        when(indexSetRegistry.isManagedIndex("graylog_3")).thenReturn(true);

        localEventBus.post(IndicesReopenedEvent.create(Collections.singleton("graylog_3")));

        final SortedSet<IndexRange> indexRanges = indexRangeService.find(begin, end);
        assertThat(indexRanges).hasSize(1);
        assertThat(indexRanges.first().indexName()).isEqualTo("graylog_3");
        assertThat(indexRanges.first().begin()).isEqualTo(begin);
        assertThat(indexRanges.first().end()).isEqualTo(end);
    }

    @Test
    @MongoDBFixtures("MongoIndexRangeServiceTest.json")
    public void testHandleIndexReopeningWhenNotManaged() throws Exception {
        final DateTime begin = new DateTime(2016, 1, 1, 0, 0, DateTimeZone.UTC);
        final DateTime end = new DateTime(2016, 1, 15, 0, 0, DateTimeZone.UTC);
        when(indexSetRegistry.isManagedIndex("graylog_3")).thenReturn(false);
        when(indices.indexRangeStatsOfIndex("graylog_3")).thenReturn(IndexRangeStats.EMPTY);

        localEventBus.post(IndicesReopenedEvent.create(Collections.singleton("graylog_3")));

        final SortedSet<IndexRange> indexRanges = indexRangeService.find(begin, end);
        assertThat(indexRanges).isEmpty();
    }
}
