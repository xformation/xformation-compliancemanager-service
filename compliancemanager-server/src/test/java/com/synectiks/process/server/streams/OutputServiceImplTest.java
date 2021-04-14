/*
 * */
package com.synectiks.process.server.streams;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.testing.mongodb.MongoDBFixtures;
import com.synectiks.process.common.testing.mongodb.MongoDBInstance;
import com.synectiks.process.server.bindings.providers.MongoJackObjectMapperProvider;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.outputs.OutputRegistry;
import com.synectiks.process.server.plugin.streams.Output;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;
import com.synectiks.process.server.streams.OutputServiceImpl;
import com.synectiks.process.server.streams.StreamService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OutputServiceImplTest {
    @Rule
    public final MongoDBInstance mongodb = MongoDBInstance.createForClass();

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private StreamService streamService;
    @Mock
    private OutputRegistry outputRegistry;

    private OutputServiceImpl outputService;

    @Before
    public void setUp() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapperProvider().get();
        final MongoJackObjectMapperProvider mapperProvider = new MongoJackObjectMapperProvider(objectMapper);
        outputService = new OutputServiceImpl(
                mongodb.mongoConnection(),
                mapperProvider,
                streamService,
                outputRegistry
        );
    }

    @Test
    @MongoDBFixtures("OutputServiceImplTest.json")
    public void loadAllReturnsAllOutputs() {
        final Set<Output> outputs = outputService.loadAll();
        assertThat(outputs).hasSize(2);
    }

    @Test
    @MongoDBFixtures("OutputServiceImplTest.json")
    public void loadByIdsReturnsRequestedOutputs() {
        assertThat(outputService.loadByIds(ImmutableSet.of())).isEmpty();
        assertThat(outputService.loadByIds(ImmutableSet.of("54e300000000000000000000"))).isEmpty();
        assertThat(outputService.loadByIds(ImmutableSet.of("54e3deadbeefdeadbeef0001", "54e300000000000000000000"))).hasSize(1);
        assertThat(outputService.loadByIds(ImmutableSet.of("54e3deadbeefdeadbeef0001", "54e3deadbeefdeadbeef0002", "54e300000000000000000000"))).hasSize(2);
    }

    @Test
    @MongoDBFixtures("OutputServiceImplTest.json")
    public void loadReturnsExistingOutput() throws NotFoundException {
        final Output output = outputService.load("54e3deadbeefdeadbeef0001");
        assertThat(output.getId()).isEqualTo("54e3deadbeefdeadbeef0001");
    }

    @Test
    @MongoDBFixtures("OutputServiceImplTest.json")
    public void loadThrowsNotFoundExceptionForNonExistingOutput() {
        assertThatThrownBy(() -> outputService.load("54e300000000000000000000"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @MongoDBFixtures("OutputServiceImplTest.json")
    public void countReturnsNumberOfOutputs() {
        assertThat(outputService.count()).isEqualTo(2L);
    }

    @Test
    @MongoDBFixtures("OutputServiceImplTest.json")
    public void countByTypeReturnsNumberOfOutputsByType() {
        assertThat(outputService.countByType())
                .hasSize(2)
                .containsEntry("org.graylog2.outputs.LoggingOutput", 1L)
                .containsEntry("org.graylog2.outputs.GelfOutput", 1L);
    }

    @Test
    @MongoDBFixtures("single-output.json")
    public void updatingOutputIsPersistent() throws Exception {
        final String outputId = "5b927d32a7c8644ed44576ed";
        final Output newOutput = outputService.update(outputId, Collections.singletonMap("title", "Some other Title"));

        assertThat(newOutput.getTitle()).isEqualTo("Some other Title");

        final Output retrievedOutput = outputService.load(outputId);

        assertThat(retrievedOutput.getTitle()).isEqualTo("Some other Title");
    }
}
