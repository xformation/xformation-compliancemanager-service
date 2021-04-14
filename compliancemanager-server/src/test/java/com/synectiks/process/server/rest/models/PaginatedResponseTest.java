/*
 * */
package com.synectiks.process.server.rest.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.revinate.assertj.json.JsonPathAssert;
import com.synectiks.process.server.database.PaginatedList;
import com.synectiks.process.server.rest.models.PaginatedResponse;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PaginatedResponseTest {
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        this.objectMapper = new ObjectMapperProvider(getClass().getClassLoader(), Collections.emptySet()).get();
    }

    @Test
    public void serialize() throws Exception {
        final ImmutableList<String> values = ImmutableList.of("hello", "world");
        final PaginatedList<String> paginatedList = new PaginatedList<>(values, values.size(), 1, 10);
        final PaginatedResponse<String> response = PaginatedResponse.create("foo", paginatedList);

        final DocumentContext ctx = JsonPath.parse(objectMapper.writeValueAsString(response));
        final JsonPathAssert jsonPathAssert = JsonPathAssert.assertThat(ctx);

        jsonPathAssert.jsonPathAsInteger("$.total").isEqualTo(2);
        jsonPathAssert.jsonPathAsInteger("$.count").isEqualTo(2);
        jsonPathAssert.jsonPathAsInteger("$.page").isEqualTo(1);
        jsonPathAssert.jsonPathAsInteger("$.per_page").isEqualTo(10);
        jsonPathAssert.jsonPathAsString("$.foo[0]").isEqualTo("hello");
        jsonPathAssert.jsonPathAsString("$.foo[1]").isEqualTo("world");
        assertThatThrownBy(() -> jsonPathAssert.jsonPathAsString("$.context")).isInstanceOf(PathNotFoundException.class);
    }

    @Test
    public void serializeWithContext() throws Exception {
        final ImmutableList<String> values = ImmutableList.of("hello", "world");
        final ImmutableMap<String, Object> context = ImmutableMap.of("context1", "wow");
        final PaginatedList<String> paginatedList = new PaginatedList<>(values, values.size(), 1, 10);
        final PaginatedResponse<String> response = PaginatedResponse.create("foo", paginatedList, context);

        final DocumentContext ctx = JsonPath.parse(objectMapper.writeValueAsString(response));
        final JsonPathAssert jsonPathAssert = JsonPathAssert.assertThat(ctx);

        jsonPathAssert.jsonPathAsInteger("$.total").isEqualTo(2);
        jsonPathAssert.jsonPathAsInteger("$.count").isEqualTo(2);
        jsonPathAssert.jsonPathAsInteger("$.page").isEqualTo(1);
        jsonPathAssert.jsonPathAsInteger("$.per_page").isEqualTo(10);
        jsonPathAssert.jsonPathAsString("$.foo[0]").isEqualTo("hello");
        jsonPathAssert.jsonPathAsString("$.foo[1]").isEqualTo("world");
        jsonPathAssert.jsonPathAsString("$.context.context1").isEqualTo("wow");
    }

    @Test
    public void serializeWithQuery() throws Exception {
        final ImmutableList<String> values = ImmutableList.of("hello", "world");
        final PaginatedList<String> paginatedList = new PaginatedList<>(values, values.size(), 1, 10);
        final PaginatedResponse<String> response = PaginatedResponse.create("foo", paginatedList, "query1");

        final DocumentContext ctx = JsonPath.parse(objectMapper.writeValueAsString(response));
        final JsonPathAssert jsonPathAssert = JsonPathAssert.assertThat(ctx);

        jsonPathAssert.jsonPathAsString("$.query").isEqualTo("query1");
        jsonPathAssert.jsonPathAsInteger("$.total").isEqualTo(2);
        jsonPathAssert.jsonPathAsInteger("$.count").isEqualTo(2);
        jsonPathAssert.jsonPathAsInteger("$.page").isEqualTo(1);
        jsonPathAssert.jsonPathAsInteger("$.per_page").isEqualTo(10);
        jsonPathAssert.jsonPathAsString("$.foo[0]").isEqualTo("hello");
        jsonPathAssert.jsonPathAsString("$.foo[1]").isEqualTo("world");
        assertThatThrownBy(() -> jsonPathAssert.jsonPathAsString("$.context")).isInstanceOf(PathNotFoundException.class);
    }

    @Test
    public void serializeWithQueryAndContext() throws Exception {
        final ImmutableList<String> values = ImmutableList.of("hello", "world");
        final ImmutableMap<String, Object> context = ImmutableMap.of("context1", "wow");
        final PaginatedList<String> paginatedList = new PaginatedList<>(values, values.size(), 1, 10);
        final PaginatedResponse<String> response = PaginatedResponse.create("foo", paginatedList, "query1", context);

        final DocumentContext ctx = JsonPath.parse(objectMapper.writeValueAsString(response));
        final JsonPathAssert jsonPathAssert = JsonPathAssert.assertThat(ctx);

        jsonPathAssert.jsonPathAsString("$.query").isEqualTo("query1");
        jsonPathAssert.jsonPathAsInteger("$.total").isEqualTo(2);
        jsonPathAssert.jsonPathAsInteger("$.count").isEqualTo(2);
        jsonPathAssert.jsonPathAsInteger("$.page").isEqualTo(1);
        jsonPathAssert.jsonPathAsInteger("$.per_page").isEqualTo(10);
        jsonPathAssert.jsonPathAsString("$.foo[0]").isEqualTo("hello");
        jsonPathAssert.jsonPathAsString("$.foo[1]").isEqualTo("world");
        jsonPathAssert.jsonPathAsString("$.context.context1").isEqualTo("wow");
    }
}
