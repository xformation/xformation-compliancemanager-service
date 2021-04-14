/*
 * */
package com.synectiks.process.common.plugins.views.search.rest.exceptionmappers;

import org.junit.Before;
import org.junit.Test;

import com.synectiks.process.common.plugins.views.search.errors.MissingCapabilitiesException;
import com.synectiks.process.common.plugins.views.search.rest.exceptionmappers.MissingCapabilitiesExceptionMapper;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Map;

import static com.synectiks.process.common.plugins.views.search.TestData.requirementsMap;
import static org.assertj.core.api.Assertions.assertThat;


public class MissingCapabilitiesExceptionMapperTest {
    private MissingCapabilitiesExceptionMapper sut;

    @Before
    public void setUp() throws Exception {
        GuiceInjectorHolder.createInjector(Collections.emptyList());

        sut = new MissingCapabilitiesExceptionMapper();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mapsValidException() {
        Response response = sut.toResponse(new MissingCapabilitiesException(requirementsMap("one", "two")));

        assertThat(response.getStatus()).isEqualTo(409);

        Map payload = (Map) response.getEntity();
        assertThat(payload).containsOnlyKeys("error", "missing");
        assertThat((String) payload.get("error")).contains("capabilities are missing");
        assertThat((Map) payload.get("missing")).containsOnlyKeys("one", "two");
    }
}
