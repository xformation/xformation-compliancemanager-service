/*
 * */
package com.synectiks.process.server.contentpacks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.events.legacy.V20190722150700_LegacyAlertConditionMigration;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfigurationService;
import com.synectiks.process.server.alerts.AlertService;
import com.synectiks.process.server.contentpacks.ContentPackInstallationPersistenceService;
import com.synectiks.process.server.contentpacks.ContentPackService;
import com.synectiks.process.server.contentpacks.constraints.ConstraintChecker;
import com.synectiks.process.server.contentpacks.facades.EntityFacade;
import com.synectiks.process.server.contentpacks.facades.GrokPatternFacade;
import com.synectiks.process.server.contentpacks.facades.OutputFacade;
import com.synectiks.process.server.contentpacks.facades.StreamFacade;
import com.synectiks.process.server.contentpacks.model.ContentPackInstallation;
import com.synectiks.process.server.contentpacks.model.ContentPackUninstallDetails;
import com.synectiks.process.server.contentpacks.model.ContentPackUninstallation;
import com.synectiks.process.server.contentpacks.model.ContentPackV1;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityV1;
import com.synectiks.process.server.contentpacks.model.entities.NativeEntityDescriptor;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.grok.GrokPattern;
import com.synectiks.process.server.grok.GrokPatternService;
import com.synectiks.process.server.indexer.indexset.IndexSetService;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.outputs.MessageOutput;
import com.synectiks.process.server.plugin.streams.Output;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;
import com.synectiks.process.server.shared.users.UserService;
import com.synectiks.process.server.streams.OutputImpl;
import com.synectiks.process.server.streams.OutputService;
import com.synectiks.process.server.streams.StreamImpl;
import com.synectiks.process.server.streams.StreamMock;
import com.synectiks.process.server.streams.StreamRuleService;
import com.synectiks.process.server.streams.StreamService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ContentPackServiceTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Mock
    private AlertService alertService;
    @Mock
    private AlarmCallbackConfigurationService alarmCallbackConfigurationService;
    @Mock
    private StreamService streamService;
    @Mock
    private StreamRuleService streamRuleService;
    @Mock
    private IndexSetService indexSetService;
    @Mock
    private OutputService outputService;
    @Mock
    private GrokPatternService patternService;
    @Mock
    private UserService userService;
    @Mock
    private ContentPackInstallationPersistenceService contentPackInstallService;
    @Mock
    private V20190722150700_LegacyAlertConditionMigration legacyAlertConditionMigration;

    private ContentPackService contentPackService;
    private Set<PluginMetaData> pluginMetaData;
    private Map<String, MessageOutput.Factory<? extends MessageOutput>> outputFactories;
    private Map<String, MessageOutput.Factory2<? extends MessageOutput>> outputFactories2;

    private ContentPackV1 contentPack;
    private ContentPackInstallation contentPackInstallation;
    private GrokPattern grokPattern;
    private ImmutableSet<NativeEntityDescriptor> nativeEntityDescriptors;

    @Before
    public void setUp() throws Exception {
        final ContentPackInstallationPersistenceService contentPackInstallationPersistenceService =
                contentPackInstallService;
        final Set<ConstraintChecker> constraintCheckers = Collections.emptySet();
        pluginMetaData = new HashSet<>();
        outputFactories = new HashMap<>();
        outputFactories2 = new HashMap<>();
        final Map<ModelType, EntityFacade<?>> entityFacades = ImmutableMap.of(
                ModelTypes.GROK_PATTERN_V1, new GrokPatternFacade(objectMapper, patternService),
                ModelTypes.STREAM_V1, new StreamFacade(objectMapper, streamService, streamRuleService, alertService, alarmCallbackConfigurationService, legacyAlertConditionMigration, indexSetService, userService),
                ModelTypes.OUTPUT_V1, new OutputFacade(objectMapper, outputService, pluginMetaData, outputFactories, outputFactories2)
        );

        contentPackService = new ContentPackService(contentPackInstallationPersistenceService, constraintCheckers, entityFacades);

        Map<String, String> entityData = new HashMap<>(2);
        entityData.put("name", "NAME");
        entityData.put("pattern", "\\w");
        grokPattern = GrokPattern.builder()
                .pattern("\\w")
                .name("NAME")
                .build();

        JsonNode jsonData = objectMapper.convertValue(entityData, JsonNode.class);
        EntityV1 entityV1 = EntityV1.builder()
                .id(ModelId.of("12345"))
                .type(ModelTypes.GROK_PATTERN_V1)
                .data(jsonData)
                .build();
        ImmutableSet<Entity> entities = ImmutableSet.of(entityV1);
        NativeEntityDescriptor nativeEntityDescriptor = NativeEntityDescriptor
                .create(ModelId.of("12345"), "dead-beef1", ModelTypes.GROK_PATTERN_V1, "NAME");
        nativeEntityDescriptors = ImmutableSet.of(nativeEntityDescriptor);
        contentPack = ContentPackV1.builder()
                .description("test")
                .entities(entities)
                .name("test")
                .revision(1)
                .summary("")
                .vendor("")
                .url(URI.create("http://graylog.com"))
                .id(ModelId.of("dead-beef"))
                .build();
        contentPackInstallation = ContentPackInstallation.builder()
                .contentPackId(ModelId.of("dead-beef"))
                .contentPackRevision(1)
                .entities(nativeEntityDescriptors)
                .comment("Installed")
                .parameters(ImmutableMap.copyOf(Collections.emptyMap()))
                .createdAt(Instant.now())
                .createdBy("me")
                .build();
    }

    @Test
    public void resolveEntitiesWithEmptyInput() {
        final Set<EntityDescriptor> resolvedEntities = contentPackService.resolveEntities(Collections.emptySet());
        assertThat(resolvedEntities).isEmpty();
    }

    @Test
    public void resolveEntitiesWithNoDependencies() throws NotFoundException {
        final StreamMock streamMock = new StreamMock(ImmutableMap.of(
                "_id", "stream-1234",
                StreamImpl.FIELD_TITLE, "Stream Title"
        ));

        when(streamService.load("stream-1234")).thenReturn(streamMock);

        final ImmutableSet<EntityDescriptor> unresolvedEntities = ImmutableSet.of(
                EntityDescriptor.create("stream-1234", ModelTypes.STREAM_V1)
        );

        final Set<EntityDescriptor> resolvedEntities = contentPackService.resolveEntities(unresolvedEntities);
        assertThat(resolvedEntities).containsOnly(EntityDescriptor.create("stream-1234", ModelTypes.STREAM_V1));
    }

    @Test
    public void resolveEntitiesWithTransitiveDependencies() throws NotFoundException {
        final StreamMock streamMock = new StreamMock(ImmutableMap.of(
                "_id", "stream-1234",
                StreamImpl.FIELD_TITLE, "Stream Title")) {
            @Override
            public Set<Output> getOutputs() {
                return Collections.singleton(
                        OutputImpl.create(
                                "output-1234",
                                "Output Title",
                                "org.example.outputs.SomeOutput",
                                "admin",
                                Collections.emptyMap(),
                                new Date(0L),
                                null
                        )
                );
            }
        };

        when(streamService.load("stream-1234")).thenReturn(streamMock);

        final ImmutableSet<EntityDescriptor> unresolvedEntities = ImmutableSet.of(
                EntityDescriptor.create("stream-1234", ModelTypes.STREAM_V1)
        );

        final Set<EntityDescriptor> resolvedEntities = contentPackService.resolveEntities(unresolvedEntities);
        assertThat(resolvedEntities).containsOnly(
                EntityDescriptor.create("stream-1234", ModelTypes.STREAM_V1),
                EntityDescriptor.create("output-1234", ModelTypes.OUTPUT_V1)
        );
    }

    @Test
    public void uninstallContentPack() throws NotFoundException {
        /* Test successful uninstall */
        when(patternService.load("dead-beef1")).thenReturn(grokPattern);
        ContentPackUninstallation expectSuccess = ContentPackUninstallation.builder()
                .skippedEntities(ImmutableSet.of())
                .failedEntities(ImmutableSet.of())
                .entities(nativeEntityDescriptors)
                .build();

        ContentPackUninstallation resultSuccess = contentPackService.uninstallContentPack(contentPack, contentPackInstallation);
        assertThat(resultSuccess).isEqualTo(expectSuccess);

       /* Test skipped uninstall */
        when(contentPackInstallService.countInstallationOfEntityById(ModelId.of("dead-beef1"))).thenReturn((long) 2);
        ContentPackUninstallation expectSkip = ContentPackUninstallation.builder()
                .skippedEntities(nativeEntityDescriptors)
                .failedEntities(ImmutableSet.of())
                .entities(ImmutableSet.of())
                .build();
        ContentPackUninstallation resultSkip = contentPackService.uninstallContentPack(contentPack, contentPackInstallation);
        assertThat(resultSkip).isEqualTo(expectSkip);

        /* Test skipped uninstall */
        when(contentPackInstallService.countInstallationOfEntityById(ModelId.of("dead-beef1"))).thenReturn((long) 1);
        when(contentPackInstallService.countInstallationOfEntityByIdAndFoundOnSystem(ModelId.of("dead-beef1"))).thenReturn((long) 1);
        ContentPackUninstallation expectSkip2 = ContentPackUninstallation.builder()
                .skippedEntities(nativeEntityDescriptors)
                .failedEntities(ImmutableSet.of())
                .entities(ImmutableSet.of())
                .build();
        ContentPackUninstallation resultSkip2 = contentPackService.uninstallContentPack(contentPack, contentPackInstallation);
        assertThat(resultSkip2).isEqualTo(expectSkip2);

        /* Test not found while uninstall */
        when(contentPackInstallService.countInstallationOfEntityById(ModelId.of("dead-beef1"))).thenReturn((long) 1);
        when(contentPackInstallService.countInstallationOfEntityByIdAndFoundOnSystem(ModelId.of("dead-beef1"))).thenReturn((long) 0);
        when(patternService.load("dead-beef1")).thenThrow(new NotFoundException("Not found."));
        ContentPackUninstallation expectFailure = ContentPackUninstallation.builder()
                .skippedEntities(ImmutableSet.of())
                .failedEntities(ImmutableSet.of())
                .entities(ImmutableSet.of())
                .build();

        ContentPackUninstallation resultFailure = contentPackService.uninstallContentPack(contentPack, contentPackInstallation);
        assertThat(resultFailure).isEqualTo(expectFailure);
    }

    @Test
    public void getUninstallDetails() throws NotFoundException {
        /* Test will be uninstalled */
        when(contentPackInstallService.countInstallationOfEntityById(ModelId.of("dead-beef1"))).thenReturn((long) 1);
        ContentPackUninstallDetails expect = ContentPackUninstallDetails.create(nativeEntityDescriptors);
        ContentPackUninstallDetails result = contentPackService.getUninstallDetails(contentPack, contentPackInstallation);
        assertThat(result).isEqualTo(expect);

        /* Test nothing will be uninstalled */
        when(contentPackInstallService.countInstallationOfEntityById(ModelId.of("dead-beef1"))).thenReturn((long) 2);
        ContentPackUninstallDetails expectNon = ContentPackUninstallDetails.create(ImmutableSet.of());
        ContentPackUninstallDetails resultNon = contentPackService.getUninstallDetails(contentPack, contentPackInstallation);
        assertThat(resultNon).isEqualTo(expectNon);
    }
}
