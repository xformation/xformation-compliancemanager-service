/*
 * */
package com.synectiks.process.common.plugins.views.search.rest.exceptionmappers;

import org.junit.Before;
import org.junit.Test;

import com.synectiks.process.common.plugins.views.search.errors.PermissionException;
import com.synectiks.process.common.plugins.views.search.rest.exceptionmappers.PermissionExceptionMapper;
import com.synectiks.process.server.plugin.rest.ApiError;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;

import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class PermissionExceptionMapperTest {
    private PermissionExceptionMapper sut;

    @Before
    public void setUp() throws Exception {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
        sut = new PermissionExceptionMapper();
    }

    @Test
    public void responseHasStatus403() {
        Response response = sut.toResponse(new PermissionException(""));

        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    public void responseHasMessageFromException() {
        PermissionException exception = new PermissionException("a message to you rudy");

        Response response = sut.toResponse(exception);

        assertThat(((ApiError) response.getEntity()).message()).isEqualTo(exception.getMessage());
    }
}
