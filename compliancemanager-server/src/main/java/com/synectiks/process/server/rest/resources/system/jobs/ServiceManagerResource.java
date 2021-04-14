/*
 * */
package com.synectiks.process.server.rest.resources.system.jobs;

import com.codahale.metrics.annotation.Timed;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.synectiks.process.server.shared.rest.resources.RestResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@RequiresAuthentication
@Api(value = "System/ServiceManager", description = "ServiceManager Status")
@Path("/system/serviceManager")
public class ServiceManagerResource extends RestResource {
    private final ServiceManager serviceManager;

    @Inject
    public ServiceManagerResource(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @GET
    @Timed
    @ApiOperation(value = "List current status of ServiceManager")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<Service, Long> list() {
        return serviceManager.startupTimes();
    }
}
