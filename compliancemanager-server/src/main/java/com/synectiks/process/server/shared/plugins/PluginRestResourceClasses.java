/*
 * */
package com.synectiks.process.server.shared.plugins;

import javax.inject.Inject;

import com.synectiks.process.server.plugin.rest.PluginRestResource;

import java.util.Map;
import java.util.Set;

/**
 * This class provides the map of {@link PluginRestResource} classes that are available through the Guice mapbinder.
 *
 * We need this wrapper class to be able to inject the {@literal Set<Class<? extends PluginRestResource>>} into
 * a Jersey REST resource. HK2 does not allow to inject this directly into the resource class.
 */
public class PluginRestResourceClasses {
    private final Map<String, Set<Class<? extends PluginRestResource>>> pluginRestResources;

    @Inject
    public PluginRestResourceClasses(final Map<String, Set<Class<? extends PluginRestResource>>> pluginRestResources) {
        this.pluginRestResources = pluginRestResources;
    }

    /**
     * Returns a map of plugin packge names to Sets of {@link PluginRestResource} classes.
     *
     * @return the map
     */
    public Map<String, Set<Class<? extends PluginRestResource>>> getMap() {
        return pluginRestResources;
    }
}
