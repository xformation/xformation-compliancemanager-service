/*
 * */
package com.synectiks.process.server.plugin;

import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;

public abstract class VersionAwareModule extends PluginModule {
    protected  <T> LinkedBindingBuilder<T> bindForVersion(Version supportedVersion, Class<T> interfaceClass) {
        return mapBinder(interfaceClass).addBinding(supportedVersion);
    }

    private <T> MapBinder<Version, T> mapBinder(Class<T> interfaceClass) {
        return MapBinder.newMapBinder(binder(), Version.class, interfaceClass);
    }

    protected  <T> LinkedBindingBuilder<T> bindForVersion(Version supportedVersion, TypeLiteral<T> interfaceClass) {
        return mapBinder(interfaceClass).addBinding(supportedVersion);
    }

    private <T> MapBinder<Version, T> mapBinder(TypeLiteral<T> interfaceClass) {
        return MapBinder.newMapBinder(binder(), new TypeLiteral<Version>() {}, interfaceClass);
    }
}
