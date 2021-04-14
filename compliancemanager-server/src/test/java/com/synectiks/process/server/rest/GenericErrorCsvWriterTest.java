/*
 * */
package com.synectiks.process.server.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synectiks.process.server.plugin.database.validators.ValidationResult;
import com.synectiks.process.server.plugin.rest.ApiError;
import com.synectiks.process.server.plugin.rest.ValidationApiError;
import com.synectiks.process.server.rest.GenericErrorCsvWriter;
import com.synectiks.process.server.rest.resources.search.responses.QueryParseError;
import com.synectiks.process.server.rest.resources.search.responses.SearchError;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericErrorCsvWriterTest extends JerseyTest {
    @Override
    protected Application configure() {
        forceSet(TestProperties.CONTAINER_PORT, "0");
        return new ResourceConfig(Resource.class, GenericErrorCsvWriter.class);
    }

    @BeforeClass
    public static void setUpInjector() {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
    }

    @Test
    public void testApiError() {
        final Response response = target("/api-error").request("text/csv").get();

        assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).isEqualTo("text/csv");
        assertThat(response.readEntity(String.class))
                .isEqualToNormalizingNewlines("\"message\"\n\"Test\"\n");
    }

    @Test
    public void testValidationApiError() {
        final Response response = target("validation-api-error").request("text/csv").get();

        assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).isEqualTo("text/csv");
        assertThat(response.readEntity(String.class))
                .isEqualToNormalizingNewlines("\"message\"\n\"Test\"\n");
    }

    @Test
    public void testSearchError() {
        final Response response = target("search-error").request("text/csv").get();

        assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).isEqualTo("text/csv");
        assertThat(response.readEntity(String.class))
                .isEqualToNormalizingNewlines("\"details\",\"message\"\n\"detail1;detail2\",\"Test\"\n");
    }

    @Test
    public void testQueryParseError() {
        final Response response = target("query-parse-error").request("text/csv").get();

        assertThat(response.getHeaderString(HttpHeaders.CONTENT_TYPE)).isEqualTo("text/csv");
        assertThat(response.readEntity(String.class))
                .isEqualToNormalizingNewlines("\"column\",\"details\",\"line\",\"message\"\n42,\"detail1;detail2\",23,\"Test\"\n");
    }

    @Path("/")
    public static class Resource {
        @GET
        @Path("api-error")
        public Response getApiError() {
            final ApiError entity = ApiError.create("Test");
            return Response.ok(entity).build();
        }

        @GET
        @Path("validation-api-error")
        public Response getValidationApiError() {
            Map<String, List<ValidationResult>> validationsErrors = Collections.singletonMap("details", Arrays.asList(
                    new ValidationResult.ValidationFailed("errors"),
                    new ValidationResult.ValidationFailed("errors")));
            final ValidationApiError entity = ValidationApiError.create("Test", validationsErrors);
            return Response.ok(entity).build();
        }

        @GET
        @Path("search-error")
        public Response getSearchError() {
            final SearchError entity = SearchError.create("Test", Arrays.asList("detail1", "detail2"));
            return Response.ok(entity).build();
        }

        @GET
        @Path("query-parse-error")
        public Response getQueryParseError() {
            final QueryParseError entity = QueryParseError.create("Test", Arrays.asList("detail1", "detail2"), 23, 42);
            return Response.ok(entity).build();
        }
    }
}