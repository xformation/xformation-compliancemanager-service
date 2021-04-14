/*
 * */
package com.synectiks.process.server.storage.providers;

import com.synectiks.process.server.indexer.cluster.ClusterAdapter;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.VersionAwareProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class ClusterAdapterProvider extends VersionAwareProvider<ClusterAdapter> {
    @Inject
    public ClusterAdapterProvider(@ElasticsearchVersion Version version, Map<Version, Provider<ClusterAdapter>> pluginBindings) {
        super(version, pluginBindings);
    }
}
