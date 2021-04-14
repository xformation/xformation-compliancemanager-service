/*
 * */
package com.synectiks.process.server.indexer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zafarkhaja.semver.Version;
import com.synectiks.process.server.indexer.IndexMappingFactory;
import com.synectiks.process.server.indexer.IndexMappingTemplate;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IndexMappingTest {

    private static final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    private IndexSetConfig indexSetConfig;

    @BeforeEach
    void setUp() {
        indexSetConfig = mock(IndexSetConfig.class);
        when(indexSetConfig.indexAnalyzer()).thenReturn("standard");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "5.0.0",
            "6.0.0",
            "7.0.0"
    })
    void createsValidMappingTemplates(String versionString) throws Exception {
        final Version version = Version.valueOf(versionString);
        final IndexMappingTemplate mapping = IndexMappingFactory.indexMappingFor(version);

        final Map<String, Object> template = mapping.toTemplate(indexSetConfig, "sampleIndexTemplate");
        final String fixture = fixtureFor(version);

        JSONAssert.assertEquals(json(template), fixture, true);
    }

    private String fixtureFor(Version version) {
        final String fixtureFileName = String.format(Locale.ENGLISH, "expected_template%s.json", version.getMajorVersion());
        return resourceFile(fixtureFileName);
    }

    private String json(Object value) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
    }

    private String resourceFile(String filename) {
        try {
            final URL resource = this.getClass().getResource(filename);
            if (resource == null) {
                Assert.fail("Unable to find resource file for test: " + filename);
            }
            final Path path = Paths.get(resource.toURI());
            final byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
