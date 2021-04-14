/*
 * */
package com.synectiks.process.server.shared.rest;

import com.google.common.base.Joiner;
import com.synectiks.process.server.rest.RestTools;

import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Configuration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PrintModelProcessor implements ModelProcessor {
    private static final Logger LOG = LoggerFactory.getLogger("REST API");

    @Override
    public ResourceModel processResourceModel(ResourceModel resourceModel, Configuration configuration) {
        LOG.debug("Map for resource model <" + resourceModel + ">:");
        final List<Resource> resources = new ArrayList<>();

        for (Resource resource : resourceModel.getResources()) {
            resources.add(resource);
            resources.addAll(findChildResources(resource));
        }

        logResources(resources);

        return resourceModel;
    }

    @Override
    public ResourceModel processSubResource(ResourceModel subResourceModel, Configuration configuration) {
        LOG.debug("Map for sub-resource model <" + subResourceModel + ">:");
        logResources(subResourceModel.getResources());

        return subResourceModel;
    }

    private void logResources(List<Resource> resources) {
        final List<ResourceDescription> resourceDescriptions = new ArrayList<>();
        for (Resource resource : resources) {
            for (ResourceMethod resourceMethod : resource.getAllMethods()) {
                final String path = RestTools.getPathFromResource(resource);
                resourceDescriptions.add(new ResourceDescription(resourceMethod.getHttpMethod(), path, resource.getHandlerClasses()));
            }
        }

        Collections.sort(resourceDescriptions);
        for (ResourceDescription resource : resourceDescriptions) {
            LOG.debug(resource.toString());
        }
    }

    private List<Resource> findChildResources(Resource parentResource) {
        final List<Resource> childResources = new ArrayList<>();
        for (Resource resource : parentResource.getChildResources()) {
            childResources.add(resource);
            childResources.addAll(findChildResources(resource));
        }

        return childResources;
    }

    private static class ResourceDescription implements Comparable<ResourceDescription> {
        private String method;
        private String path;
        private Set<Class<?>> handlerClasses;

        private ResourceDescription(String method, String path, Set<Class<?>> handlerClasses) {
            this.method = method;
            this.path = path;
            this.handlerClasses = handlerClasses;
        }

        @Override
        public int compareTo(final ResourceDescription o) {
            if (this.path.compareTo(o.path) == 0) {
                return this.method.compareTo(o.method);
            } else {
                return this.path.compareTo(o.path);
            }
        }

        @Override
        public String toString() {
            return String.format(Locale.ENGLISH, "    %-7s %s (%s)", method, path, Joiner.on(", ").join(handlerClasses));
        }
    }
}
