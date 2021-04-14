/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.ModelVersion;
import com.synectiks.process.server.contentpacks.model.constraints.GraylogVersionConstraint;
import com.synectiks.process.server.contentpacks.model.entities.Entity;
import com.synectiks.process.server.contentpacks.model.entities.EntityV1;
import com.synectiks.process.server.jackson.AutoValueSubtypeResolver;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class EntityTest {
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapperProvider().get();
        objectMapper.setSubtypeResolver(new AutoValueSubtypeResolver());
    }

    @Test
    public void serializeEntityV1() {
        final ObjectNode entityData = objectMapper.createObjectNode()
                .put("bool", true)
                .put("double", 1234.5678D)
                .put("long", 1234L)
                .put("string", "foobar");

        final EntityV1 entity = EntityV1.builder()
                .id(ModelId.of("fafd32d1-7f71-41a8-89f5-53c9b307d4d5"))
                .type(ModelTypes.INPUT_V1)
                .version(ModelVersion.of("1"))
                .data(entityData)
                .build();


        final JsonNode jsonNode = objectMapper.convertValue(entity, JsonNode.class);
        assertThat(jsonNode).isNotNull();
        assertThat(jsonNode.path("id").asText()).isEqualTo("fafd32d1-7f71-41a8-89f5-53c9b307d4d5");
        assertThat(jsonNode.path("type").path("name").asText()).isEqualTo("input");
        assertThat(jsonNode.path("type").path("version").asText()).isEqualTo("1");
        assertThat(jsonNode.path("v").asText()).isEqualTo("1");
        final JsonNode dataNode = jsonNode.path("data");
        assertThat(dataNode.isObject()).isTrue();
        assertThat(dataNode.path("bool").asBoolean()).isEqualTo(true);
        assertThat(dataNode.path("double").asDouble()).isEqualTo(1234.5678D);
        assertThat(dataNode.path("long").asLong()).isEqualTo(1234L);
        assertThat(dataNode.path("string").asText()).isEqualTo("foobar");
    }

    @Test
    public void deserializeEntityV1() throws IOException {
        final JsonNode expectedData = objectMapper.createObjectNode()
                .put("title", "GELF Input")
                .put("type", "org.graylog2.inputs.gelf.udp.GELFUDPInput")
                .setAll(ImmutableMap.of(
                        "extractors", objectMapper.createArrayNode(),
                        "static_fields", objectMapper.createArrayNode(),
                        "configuration", objectMapper.createObjectNode()
                                .put("port", "$GELF_PORT$")
                                .put("bind_address", "0.0.0.0")
                ));

        final URL resourceUrl = Resources.getResource(EntityTest.class, "entity_reference.json");
        final Entity entity = objectMapper.readValue(resourceUrl, Entity.class);
        assertThat(entity).isInstanceOf(EntityV1.class);

        final EntityV1 entityV1 = (EntityV1) entity;
        assertThat(entityV1).isNotNull();
        assertThat(entityV1.version()).isEqualTo(ModelVersion.of("1"));
        assertThat(entityV1.type()).isEqualTo(ModelTypes.INPUT_V1);
        assertThat(entityV1.id()).isEqualTo(ModelId.of("78547c87-af21-4292-8e57-614da5baf6c3"));
        assertThat(entityV1.data()).isEqualTo(expectedData);
        assertThat(entityV1.constraints()).contains(GraylogVersionConstraint.of(Version.from(3,0,0)));
    }
}