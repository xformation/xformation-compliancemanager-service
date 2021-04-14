/*
 * */
package com.synectiks.process.server.storage.providers;

import com.synectiks.process.server.indexer.searches.SearchesAdapter;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.VersionAwareProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class SearchesAdapterProvider extends VersionAwareProvider<SearchesAdapter> {
    @Inject
    public SearchesAdapterProvider(@ElasticsearchVersion Version version, Map<Version, Provider<SearchesAdapter>> pluginBindings) {
        super(version, pluginBindings);
    }
}
