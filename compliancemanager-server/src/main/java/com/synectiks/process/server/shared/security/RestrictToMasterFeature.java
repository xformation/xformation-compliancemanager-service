/*
 * */
package com.synectiks.process.server.shared.security;

import javax.inject.Inject;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import com.synectiks.process.server.plugin.ServerStatus;

import java.lang.reflect.Method;

public class RestrictToMasterFeature implements DynamicFeature {
    private final ServerStatus serverStatus;
    private final RestrictToMasterFilter restrictToMasterFilter;

    @Inject
    public RestrictToMasterFeature(ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
        this.restrictToMasterFilter = new RestrictToMasterFilter();
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        final Class<?> resourceClass = resourceInfo.getResourceClass();
        final Method resourceMethod = resourceInfo.getResourceMethod();

        if (serverStatus.hasCapability(ServerStatus.Capability.MASTER))
            return;

        if (resourceMethod.isAnnotationPresent(RestrictToMaster.class) || resourceClass.isAnnotationPresent(RestrictToMaster.class)) {
            context.register(restrictToMasterFilter);
        }
    }
}
