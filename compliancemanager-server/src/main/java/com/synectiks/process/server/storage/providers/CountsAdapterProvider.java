/*
 * */
package com.synectiks.process.server.storage.providers;

import com.synectiks.process.server.indexer.counts.CountsAdapter;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.VersionAwareProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class CountsAdapterProvider extends VersionAwareProvider<CountsAdapter> {
    @Inject
    public CountsAdapterProvider(@ElasticsearchVersion Version version, Map<Version, Provider<CountsAdapter>> pluginBindings) {
        super(version, pluginBindings);
    }
}
