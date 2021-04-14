/*
 * */
package com.synectiks.process.server.indexer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.synectiks.process.server.indexer.indexset.IndexSetConfig;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.glassfish.grizzly.utils.Charsets;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract class GIMMappingTest {
    private static final ObjectMapper mapper = new ObjectMapperProvider().get();

    String json(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    IndexSetConfig mockIndexSetConfig() {
        final IndexSetConfig indexSetConfig = mock(IndexSetConfig.class);
        when(indexSetConfig.indexAnalyzer()).thenReturn("standard");

        return indexSetConfig;
    }

    String resource(String filename) throws IOException {
        return Resources.toString(Resources.getResource(this.getClass(), filename), Charsets.UTF8_CHARSET);
    }
}
