/*
 * */
package com.synectiks.process.server.jersey;

import com.google.common.collect.ImmutableMap;
import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceModel;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.ext.Provider;
import java.util.Map;
import java.util.Optional;

@Provider
public class PrefixAddingModelProcessor implements ModelProcessor {
    private final Map<String, String> packagePrefixes;

    public PrefixAddingModelProcessor(Map<String, String> packagePrefixes) {
        this.packagePrefixes = ImmutableMap.copyOf(packagePrefixes);
    }

    @Override
    public ResourceModel processResourceModel(ResourceModel model, Configuration config) {
        // Create new resource model.
        final ResourceModel.Builder resourceModelBuilder = new ResourceModel.Builder(false);
        for (final Resource resource : model.getResources()) {
            for (Class handlerClass : resource.getHandlerClasses()) {
                final String packageName = handlerClass.getPackage().getName();

                final Optional<String> packagePrefix = packagePrefixes.entrySet().stream()
                        .filter(entry -> packageName.startsWith(entry.getKey()))
                        .sorted((o1, o2) -> -o1.getKey().compareTo(o2.getKey()))
                        .map(Map.Entry::getValue)
                        .findFirst();

                if (packagePrefix.isPresent()) {
                    final String prefixedPath = prefixPath(packagePrefix.get(), resource.getPath());
                    final Resource newResource = Resource.builder(resource)
                            .path(prefixedPath)
                            .build();

                    resourceModelBuilder.addResource(newResource);
                } else {
                    resourceModelBuilder.addResource(resource);
                }
            }
        }

        return resourceModelBuilder.build();
    }

    private String prefixPath(String prefix, String path) {
        final String sanitizedPrefix = prefix.endsWith("/") ? prefix : prefix + "/";
        final String sanitizedPath = path.startsWith("/") ? path.substring(1) : path;
        return sanitizedPrefix + sanitizedPath;
    }

    @Override
    public ResourceModel processSubResource(ResourceModel model, Configuration config) {
        return model;
    }
}