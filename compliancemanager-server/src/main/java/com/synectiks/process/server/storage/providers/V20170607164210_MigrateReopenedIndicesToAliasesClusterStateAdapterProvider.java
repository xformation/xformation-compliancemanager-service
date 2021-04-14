/*
 * */
package com.synectiks.process.server.storage.providers;

import com.synectiks.process.server.migrations.V20170607164210_MigrateReopenedIndicesToAliases;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.VersionAwareProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class V20170607164210_MigrateReopenedIndicesToAliasesClusterStateAdapterProvider extends VersionAwareProvider<V20170607164210_MigrateReopenedIndicesToAliases.ClusterState> {
    @Inject
    public V20170607164210_MigrateReopenedIndicesToAliasesClusterStateAdapterProvider(@ElasticsearchVersion Version version, Map<Version, Provider<V20170607164210_MigrateReopenedIndicesToAliases.ClusterState>> pluginBindings) {
        super(version, pluginBindings);
    }
}
