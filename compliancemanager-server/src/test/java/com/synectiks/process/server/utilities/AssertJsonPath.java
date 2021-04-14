/*
 * */
package com.synectiks.process.server.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.revinate.assertj.json.JsonPathAssert;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import java.util.function.Consumer;

public class AssertJsonPath {
    private static final ObjectMapper objectMapper = new ObjectMapperProvider().get();
    private static final Configuration configuration = Configuration.builder()
            .mappingProvider(new JacksonMappingProvider(objectMapper))
            .jsonProvider(new JacksonJsonProvider(objectMapper))
            .build();


    public static void assertJsonPath(Object obj, Consumer<JsonPathAssert> consumer) {
        assertJsonPath(obj.toString(), consumer);
    }

    public static void assertJsonPath(String json, Consumer<JsonPathAssert> consumer) {
        final DocumentContext context = JsonPath.parse(json, configuration);
        final JsonPathAssert jsonPathAssert = JsonPathAssert.assertThat(context);

        consumer.accept(jsonPathAssert);
    }
}
