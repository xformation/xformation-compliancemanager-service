/*
 * */
package com.synectiks.process.server.storage.providers;

import com.synectiks.process.server.indexer.cluster.NodeAdapter;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.VersionAwareProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class NodeAdapterProvider extends VersionAwareProvider<NodeAdapter> {
    @Inject
    public NodeAdapterProvider(@ElasticsearchVersion Version version, Map<Version, Provider<NodeAdapter>> pluginBindings) {
        super(version, pluginBindings);
    }
}
