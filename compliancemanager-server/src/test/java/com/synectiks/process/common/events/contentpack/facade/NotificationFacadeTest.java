/*
 * */
package com.synectiks.process.common.events.contentpack.facade;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.synectiks.process.common.events.contentpack.entities.EmailEventNotificationConfigEntity;
import com.synectiks.process.common.events.contentpack.entities.HttpEventNotificationConfigEntity;
import com.synectiks.process.common.events.contentpack.entities.NotificationEntity;
import com.synectiks.process.common.events.contentpack.facade.NotificationFacade;
import com.synectiks.process.common.events.notifications.DBNotificationService;
import com.synectiks.process.common.events.notifications.NotificationDto;
import com.synectiks.process.common.events.notifications.NotificationResourceHandler;
import com.synectiks.process.common.events.notifications.types.EmailEventNotificationConfig;
import com.synectiks.process.common.events.notifications.types.HTTPEventNotificationConfig;
import com.synectiks.process.common.events.processor.DBEventDefinitionService;
import com.synectiks.process.common.events.processor.DBEventProcessorStateService;
import com.synectiks.process.common.scheduler.DBJobDefinitionService;
import com.synectiks.process.common.scheduler.JobDefinitionDto;
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
import com.synectiks.process.server.security.PasswordAlgorithmFactory;
import com.synectiks.process.server.shared.SuppressForbidden;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;
import com.synectiks.process.server.shared.security.Permissions;
import com.synectiks.process.server.shared.users.UserService;
import com.synectiks.process.server.users.UserImpl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationFacadeTest {

    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    private ObjectMapper objectMapper = new ObjectMapperProvider().get();

    private NotificationFacade facade;

    @Mock
    private DBJobDefinitionService jobDefinitionService;

    @Mock
    private DBEventProcessorStateService stateService;

    @Mock
    private DBEventDefinitionService eventDefinitionService;

    @Mock
    private DBNotificationService notificationService;

    @Mock
    private NotificationResourceHandler notificationResourceHandler;

    @Mock
    private UserService userService;

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private MongoJackObjectMapperProvider mapperProvider = new MongoJackObjectMapperProvider(objectMapper);

    @Before
    @SuppressForbidden("Using Executors.newSingleThreadExecutor() is okay in tests")
    public void setUp() throws Exception {
        objectMapper.registerSubtypes(
                EmailEventNotificationConfig.class,
                EmailEventNotificationConfigEntity.class,
                HttpEventNotificationConfigEntity.class,
                HTTPEventNotificationConfig.class
        );
        jobDefinitionService = mock(DBJobDefinitionService.class);
        stateService = mock(DBEventProcessorStateService.class);
        eventDefinitionService = new DBEventDefinitionService(mongodb.mongoConnection(), mapperProvider, stateService, mock(EntityOwnershipService.class));

        notificationService = new DBNotificationService(mongodb.mongoConnection(), mapperProvider, mock(EntityOwnershipService.class));
        notificationResourceHandler = new NotificationResourceHandler(notificationService, jobDefinitionService, eventDefinitionService, Maps.newHashMap());
        facade = new NotificationFacade(objectMapper, notificationResourceHandler, notificationService, userService);
    }

    @Test
    @MongoDBFixtures("NotificationFacadeTest.json")
    public void exportEntity() {
        final ModelId id = ModelId.of("5d4d33753d27460ad18e0c4d");
        final EntityDescriptor descriptor = EntityDescriptor.create(id, ModelTypes.NOTIFICATION_V1);
        final EntityDescriptorIds entityDescriptorIds = EntityDescriptorIds.of(descriptor);
        final Optional<Entity> entity = facade.exportEntity(descriptor, entityDescriptorIds);
        assertThat(entity).isPresent();
        final EntityV1 entityV1 = (EntityV1) entity.get();
        final NotificationEntity notificationEntity = objectMapper.convertValue(entityV1.data(),
                NotificationEntity.class);
        assertThat(notificationEntity.title().asString()).isEqualTo("title");
        assertThat(notificationEntity.description().asString()).isEqualTo("description");
        assertThat(notificationEntity.config().type()).isEqualTo("email-notification-v1");
    }

    private EntityV1 createTestEntity() {
        final HttpEventNotificationConfigEntity httpEventNotificationConfigEntity =
                HttpEventNotificationConfigEntity.builder()
                        .url(ValueReference.of("https://hulud.net"))
                        .build();
        final NotificationEntity notificationEntity = NotificationEntity.builder()
                .title(ValueReference.of("title"))
                .description(ValueReference.of("descriptions"))
                .config(httpEventNotificationConfigEntity)
                .build();

        final JsonNode data = objectMapper.convertValue(notificationEntity, JsonNode.class);
        return EntityV1.builder()
                .data(data)
                .id(ModelId.of("beef-1337"))
                .type(ModelTypes.NOTIFICATION_V1)
                .build();
    }

    @Test
    public void createNativeEntity() {
        final EntityV1 entityV1 = createTestEntity();
        final JobDefinitionDto jobDefinitionDto = mock(JobDefinitionDto.class);

        when(jobDefinitionService.save(any(JobDefinitionDto.class))).thenReturn(jobDefinitionDto);
        final UserImpl kmerzUser = new UserImpl(mock(PasswordAlgorithmFactory.class), new Permissions(ImmutableSet.of()), ImmutableMap.of("username", "kmerz"));
        when(userService.load("kmerz")).thenReturn(kmerzUser);

        final NativeEntity<NotificationDto> nativeEntity = facade.createNativeEntity(
            entityV1,
            ImmutableMap.of(),
            ImmutableMap.of(),
            "kmerz");
        assertThat(nativeEntity).isNotNull();

        final NotificationDto notificationDto = nativeEntity.entity();
        assertThat(notificationDto.title()).isEqualTo("title");
        assertThat(notificationDto.description()).isEqualTo("descriptions");
        assertThat(notificationDto.config().type()).isEqualTo("http-notification-v1");
    }

    @Test
    @MongoDBFixtures("NotificationFacadeTest.json")
    public void loadNativeEntity() {
        final NativeEntityDescriptor nativeEntityDescriptor = NativeEntityDescriptor.create(
                ModelId.of("content-pack-id"),
                ModelId.of("5d4d33753d27460ad18e0c4d"),
                ModelTypes.NOTIFICATION_V1,
                "title");
        final Optional<NativeEntity<NotificationDto>> optionalNativeEntity = facade.loadNativeEntity(
                nativeEntityDescriptor);
        assertThat(optionalNativeEntity).isPresent();
        final NativeEntity<NotificationDto> nativeEntity = optionalNativeEntity.get();
        assertThat(nativeEntity.entity()).isNotNull();
        final NotificationDto notificationDto = nativeEntity.entity();
        assertThat(notificationDto.id()).isEqualTo("5d4d33753d27460ad18e0c4d");
    }

    @Test
    @MongoDBFixtures("NotificationFacadeTest.json")
    public void createExcerpt() {
        final Optional<NotificationDto> notificationDto = notificationService.get(
                "5d4d33753d27460ad18e0c4d");
        assertThat(notificationDto).isPresent();
        final EntityExcerpt excerpt = facade.createExcerpt(notificationDto.get());
        assertThat(excerpt.title()).isEqualTo("title");
        assertThat(excerpt.id()).isEqualTo(ModelId.of("5d4d33753d27460ad18e0c4d"));
        assertThat(excerpt.type()).isEqualTo(ModelTypes.NOTIFICATION_V1);
    }

    @Test
    @MongoDBFixtures("NotificationFacadeTest.json")
    public void listExcerpts() {
        final Set<EntityExcerpt> excerpts = facade.listEntityExcerpts();
        final EntityExcerpt excerpt = excerpts.iterator().next();
        assertThat(excerpt.title()).isEqualTo("title");
        assertThat(excerpt.id()).isEqualTo(ModelId.of("5d4d33753d27460ad18e0c4d"));
        assertThat(excerpt.type()).isEqualTo(ModelTypes.NOTIFICATION_V1);
    }

    @Test
    @MongoDBFixtures("NotificationFacadeTest.json")
    public void delete() {
        long countBefore = notificationService.streamAll().count();
        assertThat(countBefore).isEqualTo(1);

        final Optional<NotificationDto> notificationDto = notificationService.get(
                "5d4d33753d27460ad18e0c4d");
        assertThat(notificationDto).isPresent();
        facade.delete(notificationDto.get());

        long countAfter = notificationService.streamAll().count();
        assertThat(countAfter).isEqualTo(0);
    }
}
