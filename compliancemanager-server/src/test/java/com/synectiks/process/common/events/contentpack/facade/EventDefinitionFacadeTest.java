/*
 * */
package com.synectiks.process.common.events.contentpack.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.Graph;
import com.synectiks.process.common.events.conditions.Expr;
import com.synectiks.process.common.events.contentpack.entities.AggregationEventProcessorConfigEntity;
import com.synectiks.process.common.events.contentpack.entities.EventDefinitionEntity;
import com.synectiks.process.common.events.contentpack.entities.EventNotificationHandlerConfigEntity;
import com.synectiks.process.common.events.contentpack.entities.HttpEventNotificationConfigEntity;
import com.synectiks.process.common.events.contentpack.entities.NotificationEntity;
import com.synectiks.process.common.events.contentpack.facade.EventDefinitionFacade;
import com.synectiks.process.common.events.fields.EventFieldSpec;
import com.synectiks.process.common.events.fields.FieldValueType;
import com.synectiks.process.common.events.fields.providers.TemplateFieldValueProvider;
import com.synectiks.process.common.events.notifications.EventNotificationSettings;
import com.synectiks.process.common.events.notifications.NotificationDto;
import com.synectiks.process.common.events.notifications.types.HTTPEventNotificationConfig;
import com.synectiks.process.common.events.processor.DBEventDefinitionService;
import com.synectiks.process.common.events.processor.DBEventProcessorStateService;
import com.synectiks.process.common.events.processor.EventDefinitionDto;
import com.synectiks.process.common.events.processor.EventDefinitionHandler;
import com.synectiks.process.common.events.processor.aggregation.AggregationConditions;
import com.synectiks.process.common.events.processor.aggregation.AggregationEventProcessorConfig;
import com.synectiks.process.common.events.processor.aggregation.AggregationFunction;
import com.synectiks.process.common.events.processor.aggregation.AggregationSeries;
import com.synectiks.process.common.events.processor.storage.PersistToStreamsStorageHandler;
import com.synectiks.process.common.scheduler.DBJobDefinitionService;
import com.synectiks.process.common.scheduler.DBJobTriggerService;
import com.synectiks.process.common.scheduler.JobDefinitionDto;
import com.synectiks.process.common.scheduler.JobTriggerDto;
import com.synectiks.process.common.scheduler.clock.JobSchedulerClock;
import com.synectiks.process.common.security.entities.EntityOwnershipService;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityExcerpt;
import com.synectiks.process.server.contentpacks.model.entities.EntityV1;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntity;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.security.PasswordAlgorithmFactory;
import com.synectiks.process.server.shared.SuppressForbidden;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;
import com.synectiks.process.server.shared.security.Permissions;
import com.synectiks.process.server.shared.users.UserService;
import com.synectiks.process.server.users.UserImpl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventDefinitionFacadeTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    private ObjectMapper objectMapper = new ObjectMapperProvider().get();

    private EventDefinitionFacade facade;

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private MongoJackObjectMapperProvider mapperProvider = new MongoJackObjectMapperProvider(objectMapper);

    @Mock
    private DBEventProcessorStateService stateService;
    @Mock
    private DBJobDefinitionService jobDefinitionService;
    @Mock
    private DBJobTriggerService jobTriggerService;
    @Mock
    private JobSchedulerClock jobSchedulerClock;
    @Mock
    private DBEventDefinitionService eventDefinitionService;
    @Mock
    private EventDefinitionHandler eventDefinitionHandler;
    @Mock
    private UserService userService;
    @Mock
    private EntityOwnershipService entityOwnershipService;

    @Before
    @SuppressForbidden("Using Executors.newSingleThreadExecutor() is okay in tests")
    public void setUp() throws Exception {
        objectMapper.registerSubtypes(
                AggregationEventProcessorConfig.class,
                PersistToStreamsStorageHandler.Config.class,
                TemplateFieldValueProvider.Config.class,
                AggregationEventProcessorConfigEntity.class);
        stateService = mock(DBEventProcessorStateService.class);
        jobDefinitionService = mock(DBJobDefinitionService.class);
        jobTriggerService = mock(DBJobTriggerService.class);
        jobSchedulerClock = mock(JobSchedulerClock.class);
        eventDefinitionService = new DBEventDefinitionService(mongodb.mongoConnection(), mapperProvider, stateService, entityOwnershipService);
        eventDefinitionHandler = new EventDefinitionHandler(
                eventDefinitionService, jobDefinitionService, jobTriggerService, jobSchedulerClock);
        Set<PluginMetaData> pluginMetaData = new HashSet<>();
        facade = new EventDefinitionFacade(objectMapper, eventDefinitionHandler, pluginMetaData, jobDefinitionService, eventDefinitionService, userService);
    }

    @Test
    @MongoDBFixtures("EventDefinitionFacadeTest.json")
    public void exportEntity() {
        final ModelId id = ModelId.of("5d4032513d2746703d1467f6");

        when(jobDefinitionService.getByConfigField(eq("event_definition_id"), eq(id.id())))
                .thenReturn(Optional.of(mock(JobDefinitionDto.class)));

        final EntityDescriptor descriptor = EntityDescriptor.create(id, ModelTypes.EVENT_DEFINITION_V1);
        final EntityDescriptorIds entityDescriptorIds = EntityDescriptorIds.of(descriptor);
        final Optional<Entity> entity = facade.exportEntity(descriptor, entityDescriptorIds);
        assertThat(entity).isPresent();
        final EntityV1 entityV1 = (EntityV1) entity.get();
        final EventDefinitionEntity eventDefinitionEntity = objectMapper.convertValue(entityV1.data(),
                EventDefinitionEntity.class);
        assertThat(eventDefinitionEntity.title().asString()).isEqualTo("title");
        assertThat(eventDefinitionEntity.description().asString()).isEqualTo("description");
        assertThat(eventDefinitionEntity.config().type()).isEqualTo(AggregationEventProcessorConfigEntity.TYPE_NAME);
        assertThat(eventDefinitionEntity.isScheduled().asBoolean(ImmutableMap.of())).isTrue();
    }

    @Test
    @MongoDBFixtures("EventDefinitionFacadeTest.json")
    public void exportEntityWithoutScheduling() {
        final ModelId id = ModelId.of("5d4032513d2746703d1467f6");

        when(jobDefinitionService.getByConfigField(eq("event_definition_id"), eq(id.id())))
                .thenReturn(Optional.empty());

        final EntityDescriptor descriptor = EntityDescriptor.create(id, ModelTypes.EVENT_DEFINITION_V1);
        final EntityDescriptorIds entityDescriptorIds = EntityDescriptorIds.of(descriptor);
        final Optional<Entity> entity = facade.exportEntity(descriptor, entityDescriptorIds);
        assertThat(entity).isPresent();
        final EntityV1 entityV1 = (EntityV1) entity.get();
        final EventDefinitionEntity eventDefinitionEntity = objectMapper.convertValue(entityV1.data(),
                EventDefinitionEntity.class);
        assertThat(eventDefinitionEntity.title().asString()).isEqualTo("title");
        assertThat(eventDefinitionEntity.description().asString()).isEqualTo("description");
        assertThat(eventDefinitionEntity.config().type()).isEqualTo(AggregationEventProcessorConfigEntity.TYPE_NAME);
        assertThat(eventDefinitionEntity.isScheduled().asBoolean(ImmutableMap.of())).isFalse();
    }

    private EntityV1 createTestEntity() {
        final EventFieldSpec fieldSpec = EventFieldSpec.builder()
                .dataType(FieldValueType.STRING)
                .providers(ImmutableList.of(TemplateFieldValueProvider.Config.builder().template("template").build()))
                .build();
        final Expr.Greater trueExpr = Expr.Greater.create(Expr.NumberValue.create(2), Expr.NumberValue.create(1));
        final AggregationSeries serie = AggregationSeries.create("id-deef", AggregationFunction.COUNT, "field");
        final AggregationConditions condition = AggregationConditions.builder()
                .expression(Expr.And.create(trueExpr, trueExpr))
                .build();
        final AggregationEventProcessorConfigEntity aggregationConfig = AggregationEventProcessorConfigEntity.builder()
                .query(ValueReference.of("author: \"Jane Hopper\""))
                .streams(ImmutableSet.of())
                .groupBy(ImmutableList.of("project"))
                .series(ImmutableList.of(serie))
                .conditions(condition)
                .executeEveryMs(122200000L)
                .searchWithinMs(1231312123L)
                .build();

        final EventDefinitionEntity eventDefinitionEntity = EventDefinitionEntity.builder()
                .title(ValueReference.of("title"))
                .description(ValueReference.of("description"))
                .priority(ValueReference.of(1))
                .config(aggregationConfig)
                .alert(ValueReference.of(true))
                .fieldSpec(ImmutableMap.of("fieldSpec", fieldSpec))
                .keySpec(ImmutableList.of("keyspec"))
                .notificationSettings(EventNotificationSettings.builder()
                        .gracePeriodMs(123123)
                        .backlogSize(123)
                        .build())
                .notifications(ImmutableList.of(EventNotificationHandlerConfigEntity.builder()
                        .notificationId(ValueReference.of("123123"))
                        .build()))
                .storage(ImmutableList.of())
                .build();

        final JsonNode data = objectMapper.convertValue(eventDefinitionEntity, JsonNode.class);
        return EntityV1.builder()
                .data(data)
                .id(ModelId.of("beef-1337"))
                .type(ModelTypes.EVENT_DEFINITION_V1)
                .build();
    }

    @Test
    public void createNativeEntity() {
        final EntityV1 entityV1 = createTestEntity();
        final NotificationDto notificationDto = NotificationDto.builder()
                .config(HTTPEventNotificationConfig.builder().url("https://hulud.net").build())
                .title("Notify me Senpai")
                .description("A notification for senpai")
                .id("dead-beef")
                .build();
        final EntityDescriptor entityDescriptor = EntityDescriptor.create("123123", ModelTypes.NOTIFICATION_V1);
        final ImmutableMap<EntityDescriptor, Object> nativeEntities = ImmutableMap.of(
                entityDescriptor, notificationDto);

        final JobDefinitionDto jobDefinitionDto = mock(JobDefinitionDto.class);
        final JobTriggerDto jobTriggerDto = mock(JobTriggerDto.class);
        when(jobDefinitionDto.id()).thenReturn("job-123123");
        when(jobSchedulerClock.nowUTC()).thenReturn(DateTime.now(DateTimeZone.UTC));
        when(jobDefinitionService.save(any(JobDefinitionDto.class))).thenReturn(jobDefinitionDto);
        when(jobTriggerService.create(any(JobTriggerDto.class))).thenReturn(jobTriggerDto);
        final UserImpl kmerzUser = new UserImpl(mock(PasswordAlgorithmFactory.class), new Permissions(ImmutableSet.of()), ImmutableMap.of("username", "kmerz"));
        when(userService.load("kmerz")).thenReturn(kmerzUser);


        final NativeEntity<EventDefinitionDto> nativeEntity = facade.createNativeEntity(
                entityV1,
                ImmutableMap.of(),
                nativeEntities,
                "kmerz");
        assertThat(nativeEntity).isNotNull();

        final EventDefinitionDto eventDefinitionDto = nativeEntity.entity();
        assertThat(eventDefinitionDto.title()).isEqualTo("title");
        assertThat(eventDefinitionDto.description()).isEqualTo("description");
        assertThat(eventDefinitionDto.config().type()).isEqualTo("aggregation-v1");
        // verify that ownership was registered for this entity
        verify(entityOwnershipService, times(1)).registerNewEventDefinition(nativeEntity.entity().id(), kmerzUser);
    }

    @Test
    @MongoDBFixtures("EventDefinitionFacadeTest.json")
    public void loadNativeEntity() {
        final NativeEntityDescriptor nativeEntityDescriptor = NativeEntityDescriptor
                .create(ModelId.of("content-pack-id"),
                        ModelId.of("5d4032513d2746703d1467f6"),
                        ModelTypes.EVENT_DEFINITION_V1,
                        "title");
        final Optional<NativeEntity<EventDefinitionDto>> optionalNativeEntity = facade.loadNativeEntity(nativeEntityDescriptor);
        assertThat(optionalNativeEntity).isPresent();
        final NativeEntity<EventDefinitionDto> nativeEntity = optionalNativeEntity.get();
        assertThat(nativeEntity.entity()).isNotNull();
        final EventDefinitionDto eventDefinition = nativeEntity.entity();
        assertThat(eventDefinition.id()).isEqualTo("5d4032513d2746703d1467f6");
    }

    @Test
    @MongoDBFixtures("EventDefinitionFacadeTest.json")
    public void createExcerpt() {
        final Optional<EventDefinitionDto> eventDefinitionDto = eventDefinitionService.get(
                "5d4032513d2746703d1467f6");
        assertThat(eventDefinitionDto).isPresent();
        final EntityExcerpt excerpt = facade.createExcerpt(eventDefinitionDto.get());
        assertThat(excerpt.title()).isEqualTo("title");
        assertThat(excerpt.id()).isEqualTo(ModelId.of("5d4032513d2746703d1467f6"));
        assertThat(excerpt.type()).isEqualTo(ModelTypes.EVENT_DEFINITION_V1);
    }

    @Test
    @MongoDBFixtures("EventDefinitionFacadeTest.json")
    public void listExcerpts() {
        final Set<EntityExcerpt> excerpts = facade.listEntityExcerpts();
        final EntityExcerpt excerpt = excerpts.iterator().next();
        assertThat(excerpt.title()).isEqualTo("title");
        assertThat(excerpt.id()).isEqualTo(ModelId.of("5d4032513d2746703d1467f6"));
        assertThat(excerpt.type()).isEqualTo(ModelTypes.EVENT_DEFINITION_V1);
    }

    @Test
    @MongoDBFixtures("EventDefinitionFacadeTest.json")
    public void delete() {
        long countBefore = eventDefinitionService.streamAll().count();
        assertThat(countBefore).isEqualTo(1);

        final Optional<EventDefinitionDto> eventDefinitionDto = eventDefinitionService.get(
                "5d4032513d2746703d1467f6");
        assertThat(eventDefinitionDto).isPresent();
        facade.delete(eventDefinitionDto.get());

        long countAfter = eventDefinitionService.streamAll().count();
        assertThat(countAfter).isEqualTo(0);
    }

    @Test
    @MongoDBFixtures("EventDefinitionFacadeTest.json")
    public void resolveNativeEntity() {
        EntityDescriptor eventDescriptor = EntityDescriptor
                .create("5d4032513d2746703d1467f6", ModelTypes.EVENT_DEFINITION_V1);
        EntityDescriptor streamDescriptor = EntityDescriptor
                .create("5cdab2293d27467fbe9e8a72", ModelTypes.STREAM_V1);
        Set<EntityDescriptor> expectedNodes = ImmutableSet.of(eventDescriptor, streamDescriptor);
        Graph<EntityDescriptor> graph = facade.resolveNativeEntity(eventDescriptor);
        assertThat(graph).isNotNull();
        Set<EntityDescriptor> nodes = graph.nodes();
        assertThat(nodes).isEqualTo(expectedNodes);
    }

    @Test
    @MongoDBFixtures("EventDefinitionFacadeTest.json")
    public void resolveForInstallation() {
        EntityV1 eventEntityV1 = createTestEntity();

        final NotificationEntity notificationEntity = NotificationEntity.builder()
                .title(ValueReference.of("title"))
                .description(ValueReference.of("description"))
                .config(HttpEventNotificationConfigEntity.builder()
                        .url(ValueReference.of("http://url")).build())
                .build();
        final JsonNode data = objectMapper.convertValue(notificationEntity, JsonNode.class);
        final EntityV1 notificationV1 = EntityV1.builder()
                .data(data)
                .id(ModelId.of("123123"))
                .type(ModelTypes.EVENT_DEFINITION_V1)
                .build();

        final EntityDescriptor entityDescriptor = EntityDescriptor.create("123123", ModelTypes.NOTIFICATION_V1);

        Map<String, ValueReference> parameters = ImmutableMap.of();
        Map<EntityDescriptor, Entity> entities = ImmutableMap.of(entityDescriptor, notificationV1);

        Graph<Entity> graph = facade.resolveForInstallation(eventEntityV1, parameters, entities);
        assertThat(graph).isNotNull();
        Set<Entity> expectedNodes = ImmutableSet.of(eventEntityV1, notificationV1);
        assertThat(graph.nodes()).isEqualTo(expectedNodes);
    }
}
