/*
 * */
package com.synectiks.process.server.rest.resources.system;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.configuration.ExposedConfiguration;
import com.synectiks.process.server.shared.rest.resources.RestResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RequiresAuthentication
@Api(value = "System/Configuration", description = "Read-only access to configuration settings")
@Path("/system/configuration")
@Produces(MediaType.APPLICATION_JSON)
public class ConfigurationResource extends RestResource {
    private final Configuration configuration;

    @Inject
    public ConfigurationResource(Configuration configuration) {
        this.configuration = configuration;
    }

    @GET
    @Timed
    @ApiOperation(value = "Get relevant configuration settings and their values")
    public ExposedConfiguration getRelevant() {
        return ExposedConfiguration.create(configuration);
    }
}
