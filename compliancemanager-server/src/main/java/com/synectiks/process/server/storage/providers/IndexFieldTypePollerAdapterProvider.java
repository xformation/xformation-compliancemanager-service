/*
 * */
package com.synectiks.process.server.storage.providers;

import com.synectiks.process.server.indexer.fieldtypes.IndexFieldTypePollerAdapter;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.VersionAwareProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class IndexFieldTypePollerAdapterProvider extends VersionAwareProvider<IndexFieldTypePollerAdapter> {
    @Inject
    public IndexFieldTypePollerAdapterProvider(@ElasticsearchVersion Version version, Map<Version, Provider<IndexFieldTypePollerAdapter>> pluginBindings) {
        super(version, pluginBindings);
    }
}
