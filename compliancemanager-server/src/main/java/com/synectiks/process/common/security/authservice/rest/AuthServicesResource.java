/*
 * */
package com.synectiks.process.common.security.authservice.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.synectiks.process.common.security.authservice.AuthServiceBackendDTO;
import com.synectiks.process.common.security.authservice.DBAuthServiceBackendService;
import com.synectiks.process.common.security.authservice.GlobalAuthServiceConfig;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Path("/system/authentication/services")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "System/Authentication/Services", description = "Manage authentication services")
@RequiresAuthentication
public class AuthServicesResource extends RestResource {
    private final GlobalAuthServiceConfig authServiceConfig;
    private final DBAuthServiceBackendService backendService;

    @Inject
    public AuthServicesResource(GlobalAuthServiceConfig authServiceConfig,
                                DBAuthServiceBackendService backendService) {
        this.authServiceConfig = authServiceConfig;
        this.backendService = backendService;
    }

    @GET
    @Path("active-backend")
    @ApiOperation("Get active authentication service backend")
    @RequiresPermissions(RestPermissions.AUTH_SERVICE_GLOBAL_CONFIG_READ)
    public Response get() {
        final Optional<AuthServiceBackendDTO> activeConfig = getActiveBackendConfig();

        // We cannot use an ImmutableMap because the backend value can be null
        final Map<String, Object> response = new HashMap<>();
        response.put("backend", activeConfig.orElse(null));
        response.put("context", Collections.singletonMap("backends_total", backendService.countBackends()));

        return Response.ok(response).build();
    }

    private Optional<AuthServiceBackendDTO> getActiveBackendConfig() {
        final Optional<AuthServiceBackendDTO> activeConfig = authServiceConfig.getActiveBackendConfig();

        activeConfig.ifPresent(backend -> checkPermission(RestPermissions.AUTH_SERVICE_BACKEND_READ, backend.id()));

        return activeConfig;
    }
}
