/*
 * */
package com.synectiks.process.common.security.authservice.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.synectiks.process.common.security.authservice.AuthServiceBackendDTO;
import com.synectiks.process.common.security.authservice.test.AuthServiceBackendTestRequest;
import com.synectiks.process.common.security.authservice.test.AuthServiceBackendTestService;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.plugin.rest.ValidationFailureException;
import com.synectiks.process.server.plugin.rest.ValidationResult;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/system/authentication/services/test")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "System/Authentication/Services/Test", description = "Test authentication services")
@RequiresAuthentication
public class AuthServiceTestResource extends RestResource {
    private final AuthServiceBackendTestService testService;

    @Inject
    public AuthServiceTestResource(AuthServiceBackendTestService testService) {
        this.testService = testService;
    }

    @POST
    @Path("backend/connection")
    @ApiOperation("Test authentication service backend connection")
    @RequiresPermissions(RestPermissions.AUTH_SERVICE_TEST_BACKEND_EXECUTE)
    @NoAuditEvent("Test resource - doesn't change any data")
    public Response backendConnection(@ApiParam(name = "JSON body", required = true) @NotNull AuthServiceBackendTestRequest request) {
        // We do NOT validate the backend configuration in the request here to make it possible to execute the
        // connection test with partial configuration data. This is needed in the UI when using a step-based wizard
        // and already wants to test the connection before having the user enter the complete configuration.
        return Response.ok(testService.testConnection(request)).build();
    }

    @POST
    @Path("backend/login")
    @ApiOperation("Test authentication service backend login")
    @RequiresPermissions(RestPermissions.AUTH_SERVICE_TEST_BACKEND_EXECUTE)
    @NoAuditEvent("Test resource - doesn't change any data")
    public Response backendLogin(@ApiParam(name = "JSON body", required = true) @NotNull AuthServiceBackendTestRequest request) {
        validateConfig(request.backendConfiguration());

        return Response.ok(testService.testLogin(request)).build();
    }

    private void validateConfig(AuthServiceBackendDTO config) {
        final ValidationResult result = config.validate();

        if (result.failed()) {
            throw new ValidationFailureException(result);
        }
    }

}
