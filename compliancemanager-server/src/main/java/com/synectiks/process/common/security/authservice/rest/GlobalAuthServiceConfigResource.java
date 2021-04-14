/*
 * */
package com.synectiks.process.common.security.authservice.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.synectiks.process.common.security.SecurityAuditEventTypes;
import com.synectiks.process.common.security.authservice.GlobalAuthServiceConfig;
import com.synectiks.process.server.audit.jersey.AuditEvent;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;

@Path("/system/authentication/services/configuration")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "System/Authentication/Services/Configuration", description = "Manage global authentication services configuration")
@RequiresAuthentication
public class GlobalAuthServiceConfigResource extends RestResource {
    private final GlobalAuthServiceConfig authServiceConfig;

    @Inject
    public GlobalAuthServiceConfigResource(GlobalAuthServiceConfig authServiceConfig) {
        this.authServiceConfig = authServiceConfig;
    }

    @GET
    @ApiOperation("Get global authentication services configuration")
    @RequiresPermissions(RestPermissions.AUTH_SERVICE_GLOBAL_CONFIG_READ)
    public Response get() {
        return toResponse(authServiceConfig.getConfiguration());
    }

    @POST
    @ApiOperation("Update global authentication services configuration")
    @RequiresPermissions(RestPermissions.AUTH_SERVICE_GLOBAL_CONFIG_EDIT)
    @AuditEvent(type = SecurityAuditEventTypes.AUTH_SERVICE_GLOBAL_CONFIG_UPDATE)
    public Response update(@ApiParam(name = "JSON body", required = true) @NotNull GlobalAuthServiceConfig.Data body) {
        return toResponse(authServiceConfig.updateConfiguration(body));
    }

    private Response toResponse(GlobalAuthServiceConfig.Data configuration) {
        return Response.ok(Collections.singletonMap("configuration", configuration)).build();
    }
}
