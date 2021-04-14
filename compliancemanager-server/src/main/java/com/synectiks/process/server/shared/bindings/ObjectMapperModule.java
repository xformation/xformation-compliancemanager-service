/*
 * */
package com.synectiks.process.server.shared.bindings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.synectiks.process.common.grn.GRNModule;
import com.synectiks.process.server.plugin.inject.Graylog2Module;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;
import com.synectiks.process.server.shared.plugins.GraylogClassLoader;

import static java.util.Objects.requireNonNull;

public class ObjectMapperModule extends Graylog2Module {
    private final ClassLoader classLoader;

    @VisibleForTesting
    public ObjectMapperModule() {
        this(ObjectMapperModule.class.getClassLoader());
    }

    public ObjectMapperModule(ClassLoader classLoader) {
        this.classLoader = requireNonNull(classLoader);
    }

    @Override
    protected void configure() {
        // the ObjectMapperProvider requires at least an empty JacksonSubtypes set.
        // if the multibinder wasn't created that reference will be null, so we force its creation here
        jacksonSubTypesBinder();
        install(new GRNModule());
        bind(ClassLoader.class).annotatedWith(GraylogClassLoader.class).toInstance(classLoader);
        bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).asEagerSingleton();
    }
}
