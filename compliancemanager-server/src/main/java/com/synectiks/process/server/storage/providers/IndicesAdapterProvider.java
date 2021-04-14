/*
 * */
package com.synectiks.process.server.storage.providers;

import com.synectiks.process.server.indexer.indices.IndicesAdapter;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.VersionAwareProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class IndicesAdapterProvider extends VersionAwareProvider<IndicesAdapter> {
    @Inject
    public IndicesAdapterProvider(@ElasticsearchVersion Version version, Map<Version, Provider<IndicesAdapter>> pluginBindings) {
        super(version, pluginBindings);
    }
}
