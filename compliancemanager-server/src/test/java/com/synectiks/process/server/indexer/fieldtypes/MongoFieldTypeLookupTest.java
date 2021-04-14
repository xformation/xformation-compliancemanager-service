/*
 * */
package com.synectiks.process.server.indexer.fieldtypes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.indexer.fieldtypes.FieldTypeDTO;
import com.synectiks.process.server.indexer.fieldtypes.FieldTypeMapper;
import com.synectiks.process.server.indexer.fieldtypes.FieldTypes;
import com.synectiks.process.server.indexer.fieldtypes.IndexFieldTypesDTO;
import com.synectiks.process.server.indexer.fieldtypes.IndexFieldTypesService;
import com.synectiks.process.server.indexer.fieldtypes.MongoFieldTypeLookup;
import com.synectiks.process.server.streams.StreamService;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MongoFieldTypeLookupTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    private IndexFieldTypesService dbService;
    private MongoFieldTypeLookup lookup;

    @Before
    public void setUp() throws Exception {
        final MongoJackObjectMapperProvider objectMapperProvider = new MongoJackObjectMapperProvider(new ObjectMapper());
        final StreamService streamService = mock(StreamService.class);
        this.dbService = new IndexFieldTypesService(mongodb.mongoConnection(), streamService, objectMapperProvider);
        this.lookup = new MongoFieldTypeLookup(dbService, new FieldTypeMapper());
    }

    @After
    public void tearDown() {
        mongodb.mongoConnection().getMongoDatabase().drop();
    }

    private IndexFieldTypesDTO createDto(String indexName, String indexSetId, Set<FieldTypeDTO> fields) {
        return IndexFieldTypesDTO.builder()
                .indexName(indexName)
                .indexSetId(indexSetId)
                .fields(ImmutableSet.<FieldTypeDTO>builder()
                        .add(FieldTypeDTO.create("message", "text"))
                        .add(FieldTypeDTO.create("source", "text"))
                        .add(FieldTypeDTO.create("timestamp", "date"))
                        .add(FieldTypeDTO.create("http_method", "keyword"))
                        .add(FieldTypeDTO.create("http_status", "long"))
                        .addAll(fields)
                        .build())
                .build();
    }

    @Test
    public void getSingleField() {
        dbService.save(createDto("graylog_0", "abc", Collections.emptySet()));
        dbService.save(createDto("graylog_1", "xyz", Collections.emptySet()));
        dbService.save(createDto("graylog_2", "xyz", Collections.emptySet()));
        dbService.save(createDto("graylog_3", "xyz", of(
                FieldTypeDTO.create("yolo1", "text")
        )));

        final FieldTypes result = lookup.get("message").orElse(null);

        assertThat(result).isNotNull();
        assertThat(result.fieldName()).isEqualTo("message");
        assertThat(result.types()).containsOnly(FieldTypes.Type.builder()
                .type("string")
                .properties(of("full-text-search"))
                .indexNames(of("graylog_0", "graylog_1", "graylog_2", "graylog_3"))
                .build());
    }

    @Test
    public void getMultipleFields() {
        dbService.save(createDto("graylog_0", "abc", Collections.emptySet()));
        dbService.save(createDto("graylog_1", "xyz", Collections.emptySet()));
        dbService.save(createDto("graylog_2", "xyz", of(
                FieldTypeDTO.create("yolo1", "boolean")
        )));
        dbService.save(createDto("graylog_3", "xyz", of(
                FieldTypeDTO.create("yolo1", "text")
        )));

        final Map<String, FieldTypes> result = lookup.get(of("yolo1", "timestamp"));

        assertThat(result).containsOnlyKeys("yolo1", "timestamp");

        assertThat(result.get("yolo1").fieldName()).isEqualTo("yolo1");
        assertThat(result.get("yolo1").types()).hasSize(2);
        assertThat(result.get("yolo1").types()).containsOnly(
                FieldTypes.Type.builder()
                        .type("string")
                        .properties(of("full-text-search"))
                        .indexNames(of("graylog_3"))
                        .build(),
                FieldTypes.Type.builder()
                        .type("boolean")
                        .properties(of("enumerable"))
                        .indexNames(of("graylog_2"))
                        .build()
        );

        assertThat(result.get("timestamp").fieldName()).isEqualTo("timestamp");
        assertThat(result.get("timestamp").types()).hasSize(1);
        assertThat(result.get("timestamp").types()).containsOnly(FieldTypes.Type.builder()
                .type("date")
                .properties(of("enumerable"))
                .indexNames(of("graylog_0", "graylog_1", "graylog_2", "graylog_3"))
                .build());
    }

    @Test
    public void getMultipleFieldsWithIndexScope() {
        dbService.save(createDto("graylog_0", "abc", Collections.emptySet()));
        dbService.save(createDto("graylog_1", "xyz", Collections.emptySet()));
        dbService.save(createDto("graylog_2", "xyz", of(
                FieldTypeDTO.create("yolo1", "boolean")
        )));
        dbService.save(createDto("graylog_3", "xyz", of(
                FieldTypeDTO.create("yolo1", "text")
        )));

        final Map<String, FieldTypes> result = lookup.get(of("yolo1", "timestamp"), of("graylog_1", "graylog_2"));

        assertThat(result).containsOnlyKeys("yolo1", "timestamp");

        assertThat(result.get("yolo1").fieldName()).isEqualTo("yolo1");
        assertThat(result.get("yolo1").types()).hasSize(1);
        assertThat(result.get("yolo1").types()).containsOnly(
                FieldTypes.Type.builder()
                        .type("boolean")
                        .properties(of("enumerable"))
                        .indexNames(of("graylog_2"))
                        .build()
        );

        assertThat(result.get("timestamp").fieldName()).isEqualTo("timestamp");
        assertThat(result.get("timestamp").types()).hasSize(1);
        assertThat(result.get("timestamp").types()).containsOnly(FieldTypes.Type.builder()
                .type("date")
                .properties(of("enumerable"))
                .indexNames(of("graylog_1", "graylog_2"))
                .build());
    }
}
