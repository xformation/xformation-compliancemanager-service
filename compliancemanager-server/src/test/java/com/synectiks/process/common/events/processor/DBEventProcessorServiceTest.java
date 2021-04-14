/*
 * */
package com.synectiks.process.common.events.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.events.TestEventProcessorConfig;
import com.synectiks.process.common.events.notifications.EventNotificationSettings;
import com.synectiks.process.common.events.processor.DBEventDefinitionService;
import com.synectiks.process.common.events.processor.DBEventProcessorStateService;
import com.synectiks.process.common.events.processor.EventDefinitionDto;
import com.synectiks.process.common.events.processor.storage.PersistToStreamsStorageHandler;
import com.synectiks.process.common.security.entities.EntityOwnershipService;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DBEventProcessorServiceTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private DBEventProcessorStateService stateService;

    private DBEventDefinitionService dbService;

    @Before
    public void setUp() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();
        objectMapper.registerSubtypes(new NamedType(TestEventProcessorConfig.class, TestEventProcessorConfig.TYPE_NAME));
        objectMapper.registerSubtypes(new NamedType(PersistToStreamsStorageHandler.Config.class, PersistToStreamsStorageHandler.Config.TYPE_NAME));

        this.dbService = new DBEventDefinitionService(mongodb.mongoConnection(), new MongoJackObjectMapperProvider(objectMapper), stateService, mock(EntityOwnershipService.class));
    }

    @Test
    @MongoDBFixtures("event-processors.json")
    public void loadPersisted() {
        final List<EventDefinitionDto> dtos = dbService.streamAll().collect(Collectors.toList());

        assertThat(dtos).hasSize(1);

        assertThat(dtos.get(0)).satisfies(dto -> {
            assertThat(dto.id()).isNotBlank();
            assertThat(dto.title()).isEqualTo("Test");
            assertThat(dto.description()).isEqualTo("A test event definition");
            assertThat(dto.priority()).isEqualTo(2);
            assertThat(dto.keySpec()).isEqualTo(ImmutableList.of("username"));
            assertThat(dto.fieldSpec()).isEmpty();
            assertThat(dto.notifications()).isEmpty();
            assertThat(dto.storage()).hasSize(1);

            assertThat(dto.config()).isInstanceOf(TestEventProcessorConfig.class);
            assertThat(dto.config()).satisfies(abstractConfig -> {
                final TestEventProcessorConfig config = (TestEventProcessorConfig) abstractConfig;

                assertThat(config.type()).isEqualTo("__test_event_processor_config__");
                assertThat(config.message()).isEqualTo("This is a test event processor");
            });
        });
    }

    @Test
    public void save() {
        final EventDefinitionDto newDto = EventDefinitionDto.builder()
                .title("Test")
                .description("A test event definition")
                .config(TestEventProcessorConfig.builder()
                        .message("This is a test event processor")
                        .searchWithinMs(1000)
                        .executeEveryMs(1000)
                        .build())
                .priority(3)
                .alert(false)
                .notificationSettings(EventNotificationSettings.withGracePeriod(60000))
                .keySpec(ImmutableList.of("a", "b"))
                .notifications(ImmutableList.of())
                .build();

        final EventDefinitionDto dto = dbService.save(newDto);

        assertThat(dto.id()).isNotBlank();
        assertThat(dto.title()).isEqualTo("Test");
        assertThat(dto.description()).isEqualTo("A test event definition");
        assertThat(dto.priority()).isEqualTo(3);
        assertThat(dto.keySpec()).isEqualTo(ImmutableList.of("a", "b"));
        assertThat(dto.fieldSpec()).isEmpty();
        assertThat(dto.notifications()).isEmpty();
        assertThat(dto.storage()).hasSize(1);
        // We will always add a persist-to-streams handler for now
        assertThat(dto.storage()).containsOnly(PersistToStreamsStorageHandler.Config.createWithDefaultEventsStream());
    }
}
