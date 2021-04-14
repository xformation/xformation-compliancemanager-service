/*
 * */
package com.synectiks.process.server.contentpacks.jersey;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.contentpacks.jersey.ModelIdParamConverter;
import com.synectiks.process.server.contentpacks.model.ModelId;
import com.synectiks.process.server.shared.bindings.GuiceInjectorHolder;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ModelIdParamConverterTest extends JerseyTest {
    static {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
    }

    @Override
    protected Application configure() {
        return new ResourceConfig(Resource.class, ModelIdParamConverter.Provider.class);
    }

    @Test
    public void testStringParam() {
        Form form = new Form();
        form.param("form", "formParam");
        final Response response = target().path("resource")
                .path("pathParam")
                .matrixParam("matrix", "matrixParam")
                .queryParam("query", "queryParam")
                .request()
                .header("header", "headerParam")
                .cookie("cookie", "cookieParam")
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        final String str = response.readEntity(String.class);
        assertThat(str).isEqualTo("pathParam_matrixParam_queryParam_headerParam_cookieParam_formParam");
    }


    @Path("resource")
    public static class Resource {
        @NoAuditEvent("Test")
        @POST
        @Path("{path}")
        public String modelId(@PathParam("path") ModelId path,
                              @MatrixParam("matrix") ModelId matrix,
                              @QueryParam("query") ModelId query,
                              @HeaderParam("header") ModelId header,
                              @CookieParam("cookie") ModelId cookie,
                              @FormParam("form") ModelId form) {
            return path.id()
                    + "_" + matrix.id()
                    + "_" + query.id()
                    + "_" + header.id()
                    + "_" + cookie.id()
                    + "_" + form.id();
        }
    }
}