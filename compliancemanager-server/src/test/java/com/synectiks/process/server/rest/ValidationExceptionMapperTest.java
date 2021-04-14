/*
 * */
package com.synectiks.process.server.rest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.plugin.database.ValidationException;
import com.synectiks.process.server.plugin.database.validators.ValidationResult;
import com.synectiks.process.server.plugin.rest.ValidationApiError;
import com.synectiks.process.server.rest.ValidationExceptionMapper;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;

import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationExceptionMapperTest {
    @BeforeClass
    public static void setUpInjector() {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
    }

    @Test
    public void testToResponse() throws Exception {
        final ExceptionMapper<ValidationException> mapper = new ValidationExceptionMapper();

        final Map<String, List<ValidationResult>> validationErrors = ImmutableMap.of(
            "foo", ImmutableList.of(new ValidationResult.ValidationFailed("foo failed")),
            "bar", ImmutableList.of(
                new ValidationResult.ValidationFailed("bar failed"),
                new ValidationResult.ValidationFailed("baz failed"))
        );

        @SuppressWarnings("ThrowableInstanceNeverThrown")
        final ValidationException exception = new ValidationException(validationErrors);
        final Response response = mapper.toResponse(exception);

        assertThat(response.getStatusInfo()).isEqualTo(Response.Status.BAD_REQUEST);
        assertThat(response.getMediaType()).isEqualTo(MediaType.APPLICATION_JSON_TYPE);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.getEntity()).isInstanceOf(ValidationApiError.class);

        final ValidationApiError responseEntity = (ValidationApiError) response.getEntity();
        assertThat(responseEntity.message()).startsWith("Validation failed!");
        assertThat(responseEntity.validationErrors()).containsKeys("foo", "bar");
        assertThat(responseEntity.validationErrors().get("foo")).hasSize(1);
        assertThat(responseEntity.validationErrors().get("bar")).hasSize(2);
    }
}
