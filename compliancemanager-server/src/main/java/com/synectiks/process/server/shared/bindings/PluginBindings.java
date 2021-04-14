/*
 * */
package com.synectiks.process.server.shared.bindings;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.synectiks.process.server.plugin.Plugin;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.PluginModule;
import com.synectiks.process.server.plugin.rest.PluginRestResource;

import java.util.Set;

public class PluginBindings extends AbstractModule {
    private final Set<Plugin> plugins;

    public PluginBindings(Set<Plugin> plugins) {
        this.plugins = plugins;
    }

    @Override
    protected void configure() {
        final Multibinder<Plugin> pluginbinder = Multibinder.newSetBinder(binder(), Plugin.class);
        final Multibinder<PluginMetaData> pluginMetaDataBinder = Multibinder.newSetBinder(binder(), PluginMetaData.class);

        // Make sure there is a binding for the plugin rest resource classes to avoid binding errors when running
        // without plugins.
        MapBinder.newMapBinder(binder(), new TypeLiteral<String>() {},
                new TypeLiteral<Class<? extends PluginRestResource>>() {})
                .permitDuplicates();

        for (final Plugin plugin : plugins) {
            pluginbinder.addBinding().toInstance(plugin);
            for (final PluginModule pluginModule : plugin.modules()) {
                binder().install(pluginModule);
            }

            pluginMetaDataBinder.addBinding().toInstance(plugin.metadata());
        }
    }
}
