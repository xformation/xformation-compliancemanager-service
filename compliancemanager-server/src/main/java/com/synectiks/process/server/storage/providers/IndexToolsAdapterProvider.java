/*
 * */
package com.synectiks.process.server.storage.providers;

import com.synectiks.process.server.indexer.IndexToolsAdapter;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.VersionAwareProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class IndexToolsAdapterProvider extends VersionAwareProvider<IndexToolsAdapter> {
    @Inject
    public IndexToolsAdapterProvider(@ElasticsearchVersion Version version, Map<Version, Provider<IndexToolsAdapter>> pluginBindings) {
        super(version, pluginBindings);
    }
}
