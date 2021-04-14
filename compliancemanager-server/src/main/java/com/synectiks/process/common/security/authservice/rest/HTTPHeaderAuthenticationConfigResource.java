/*
 * */
package com.synectiks.process.common.security.authservice.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.synectiks.process.server.audit.AuditEventTypes;
import com.synectiks.process.server.audit.jersey.AuditEvent;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.security.headerauth.HTTPHeaderAuthConfig;
import com.synectiks.process.server.shared.security.RestPermissions;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/system/authentication/http-header-auth-config")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "System/Authentication/HTTPHeaderAuthConfig", description = "Manage the HTTP header authentication configuration")
@RequiresAuthentication
public class HTTPHeaderAuthenticationConfigResource {
    private final ClusterConfigService clusterConfigService;

    @Inject
    public HTTPHeaderAuthenticationConfigResource(ClusterConfigService clusterConfigService) {
        this.clusterConfigService = clusterConfigService;
    }

    @GET
    @ApiOperation("Get HTTP header authentication config")
    @RequiresPermissions(RestPermissions.AUTH_HTTP_HEADER_CONFIG_READ)
    public HTTPHeaderAuthConfig getConfig() {
        return loadConfig();
    }

    @PUT
    @ApiOperation("Update HTTP header authentication config")
    @RequiresPermissions(RestPermissions.AUTH_HTTP_HEADER_CONFIG_EDIT)
    @AuditEvent(type = AuditEventTypes.AUTHENTICATION_HTTP_HEADER_CONFIG_UPDATE)
    public HTTPHeaderAuthConfig updateConfig(@Valid HTTPHeaderAuthConfig config) {
        clusterConfigService.write(config);
        return loadConfig();
    }

    private HTTPHeaderAuthConfig loadConfig() {
        return clusterConfigService.getOrDefault(HTTPHeaderAuthConfig.class, HTTPHeaderAuthConfig.createDisabled());
    }
}
