/*
 * */
package com.synectiks.process.server.contentpacks.facades;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synectiks.process.common.plugins.sidecar.services.ConfigurationService;
import com.synectiks.process.common.plugins.sidecar.services.ConfigurationVariableService;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.facades.SidecarCollectorConfigurationFacade;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.EntityV1;
import com.synectiks.process.server.contentpacks.model.entities.SidecarCollectorConfigurationEntity;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class SidecarCollectorConfigurationFacadeTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();
    private SidecarCollectorConfigurationFacade facade;

    @Before
    public void setUp() throws Exception {
        final MongoJackObjectMapperProvider mapperProvider = new MongoJackObjectMapperProvider(objectMapper);
        final ConfigurationService configurationService = new ConfigurationService(mongodb.mongoConnection(),
                mapperProvider, new ConfigurationVariableService(mongodb.mongoConnection(), mapperProvider));

        facade = new SidecarCollectorConfigurationFacade(objectMapper, configurationService);
    }

    @Test
    @MongoDBFixtures("SidecarCollectorConfigurationFacadeTest.json")
    public void exportEntity() {
        final EntityDescriptor descriptor = EntityDescriptor.create("5b17e1a53f3ab8204eea1051", ModelTypes.SIDECAR_COLLECTOR_CONFIGURATION_V1);
        final EntityDescriptor collectorDescriptor = EntityDescriptor.create("5b4c920b4b900a0024af0001", ModelTypes.SIDECAR_COLLECTOR_V1);
        final EntityDescriptorIds entityDescriptorIds = EntityDescriptorIds.of(descriptor, collectorDescriptor);
        final Entity entity = facade.exportEntity(descriptor, entityDescriptorIds).orElseThrow(AssertionError::new);

        assertThat(entity).isInstanceOf(EntityV1.class);
        assertThat(entity.id()).isEqualTo(ModelId.of(entityDescriptorIds.get(descriptor).orElse(null)));
        assertThat(entity.type()).isEqualTo(ModelTypes.SIDECAR_COLLECTOR_CONFIGURATION_V1);

        final EntityV1 entityV1 = (EntityV1) entity;
        final SidecarCollectorConfigurationEntity configEntity = objectMapper.convertValue(entityV1.data(), SidecarCollectorConfigurationEntity.class);

        assertThat(configEntity.title()).isEqualTo(ValueReference.of("filebeat config"));
        assertThat(configEntity.collectorId()).isEqualTo(ValueReference.of(entityDescriptorIds.get(collectorDescriptor).orElse(null)));
        assertThat(configEntity.color().asString(Collections.emptyMap())).isEqualTo("#ffffff");
        assertThat(configEntity.template().asString(Collections.emptyMap())).isEqualTo("empty template");
    }
}
