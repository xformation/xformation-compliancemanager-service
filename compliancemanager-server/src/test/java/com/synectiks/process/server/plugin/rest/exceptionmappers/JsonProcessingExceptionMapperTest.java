/*
 * */
package com.synectiks.process.server.plugin.rest.exceptionmappers;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.synectiks.process.server.plugin.rest.ApiError;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;
import com.synectiks.process.server.shared.rest.exceptionmappers.JsonProcessingExceptionMapper;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonProcessingExceptionMapperTest {
    @BeforeClass
    public static void setUpInjector() {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
    }

    @Test
    public void testToResponse() throws Exception {
        final ExceptionMapper<JsonProcessingException> mapper = new JsonProcessingExceptionMapper();
        final JsonParser jsonParser = new JsonFactory().createParser("");
        final JsonMappingException exception = new JsonMappingException(jsonParser, "Boom!", new RuntimeException("rootCause"));
        final Response response = mapper.toResponse(exception);

        assertEquals(Response.Status.BAD_REQUEST, response.getStatusInfo());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        assertTrue(response.hasEntity());
        assertTrue(response.getEntity() instanceof ApiError);

        final ApiError responseEntity = (ApiError) response.getEntity();
        assertTrue(responseEntity.message().startsWith("Boom!"));
    }
}
