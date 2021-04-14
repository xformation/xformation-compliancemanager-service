/*
 * */
package com.synectiks.process.server.streams;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.security.entities.EntityOwnershipService;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfigurationService;
import com.synectiks.process.server.alerts.AlertService;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.events.ClusterEventBus;
import com.synectiks.process.server.indexer.MongoIndexSet;
import com.synectiks.process.server.indexer.indexset.IndexSetService;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.plugin.streams.Output;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.streams.OutputService;
import com.synectiks.process.server.streams.StreamRuleService;
import com.synectiks.process.server.streams.StreamService;
import com.synectiks.process.server.streams.StreamServiceImpl;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StreamServiceImplTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private StreamRuleService streamRuleService;
    @Mock
    private AlertService alertService;
    @Mock
    private OutputService outputService;
    @Mock
    private IndexSetService indexSetService;
    @Mock
    private MongoIndexSet.Factory factory;
    @Mock
    private NotificationService notificationService;
    @Mock
    private AlarmCallbackConfigurationService alarmCallbackConfigurationService;
    @Mock
    private EntityOwnershipService entityOwnershipService;

    private StreamService streamService;

    @Before
    public void setUp() throws Exception {
        this.streamService = new StreamServiceImpl(mongodb.mongoConnection(), streamRuleService, alertService,
                outputService, indexSetService, factory, notificationService, entityOwnershipService, new ClusterEventBus(), alarmCallbackConfigurationService);
    }

    @Test
    public void loadAllWithConfiguredAlertConditionsShouldNotFailWhenNoStreamsArePresent() {
        final List<Stream> alertableStreams = this.streamService.loadAllWithConfiguredAlertConditions();

        assertThat(alertableStreams)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @MongoDBFixtures("someStreamsWithoutAlertConditions.json")
    public void loadAllWithConfiguredAlertConditionsShouldReturnNoStreams() {
        final List<Stream> alertableStreams = this.streamService.loadAllWithConfiguredAlertConditions();

        assertThat(alertableStreams)
                .isEmpty();
    }

    @Test
    @MongoDBFixtures({"someStreamsWithoutAlertConditions.json", "someStreamsWithAlertConditions.json"})
    public void loadAllWithConfiguredAlertConditionsShouldReturnStreams() {
        final List<Stream> alertableStreams = this.streamService.loadAllWithConfiguredAlertConditions();

        assertThat(alertableStreams)
                .isNotEmpty()
                .hasSize(2);
    }

    @Test
    @MongoDBFixtures("someStreamsWithAlertConditions.json")
    public void loadByIds() {
        assertThat(this.streamService.loadByIds(ImmutableSet.of("565f02223b0c25a537197af2"))).hasSize(1);
        assertThat(this.streamService.loadByIds(ImmutableSet.of("565f02223b0c25a5deadbeef"))).isEmpty();
        assertThat(this.streamService.loadByIds(ImmutableSet.of("565f02223b0c25a537197af2", "565f02223b0c25a5deadbeef"))).hasSize(1);
    }

    @Test
    @MongoDBFixtures("someStreamsWithoutAlertConditions.json")
    public void addOutputs() throws NotFoundException {
        final ObjectId streamId = new ObjectId("5628f4503b0c5756a8eebc4d");
        final ObjectId output1Id = new ObjectId("5628f4503b00deadbeef0001");
        final ObjectId output2Id = new ObjectId("5628f4503b00deadbeef0002");

        final Output output1 = mock(Output.class);
        final Output output2 = mock(Output.class);

        when(output1.getId()).thenReturn(output1Id.toHexString());
        when(output2.getId()).thenReturn(output2Id.toHexString());
        when(outputService.load(output1Id.toHexString())).thenReturn(output1);
        when(outputService.load(output2Id.toHexString())).thenReturn(output2);

        streamService.addOutputs(streamId, ImmutableSet.of(output1Id, output2Id));

        final Stream stream = streamService.load(streamId.toHexString());
        assertThat(stream.getOutputs())
                .anySatisfy(output -> assertThat(output.getId()).isEqualTo(output1Id.toHexString()))
                .anySatisfy(output -> assertThat(output.getId()).isEqualTo(output2Id.toHexString()));
    }
}
