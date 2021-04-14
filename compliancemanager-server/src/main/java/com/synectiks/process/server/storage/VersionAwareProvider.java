/*
 * */
package com.synectiks.process.server.storage;

import javax.inject.Inject;
import javax.inject.Provider;

import com.synectiks.process.server.plugin.Version;

import java.util.Map;

public class VersionAwareProvider<T> implements Provider<T> {
    private final Version elasticsearchMajorVersion;
    private final Map<Version, Provider<T>> pluginBindings;

    @Inject
    public VersionAwareProvider(@ElasticsearchVersion Version elasticsearchVersion, Map<Version, Provider<T>> pluginBindings) {
        this.elasticsearchMajorVersion = majorVersionFrom(elasticsearchVersion);
        this.pluginBindings = pluginBindings;
    }

    @Override
    public T get() {
        final Provider<T> provider = this.pluginBindings.get(elasticsearchMajorVersion);
        if (provider == null) {
            throw new UnsupportedElasticsearchException(elasticsearchMajorVersion);
        }
        return provider.get();
    }

    private Version majorVersionFrom(Version version) {
        return Version.from(version.getVersion().getMajorVersion(), 0, 0);
    }
}
