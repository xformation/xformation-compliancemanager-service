/*
 * */
package com.synectiks.process.server.storage.providers;

import com.synectiks.process.common.plugins.views.migrations.V20200730000000_AddGl2MessageIdFieldAliasForEvents;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.VersionAwareProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class V20200730000000_AddGl2MessageIdFieldAliasForEventsElasticsearchAdapterProvider
        extends VersionAwareProvider<V20200730000000_AddGl2MessageIdFieldAliasForEvents.ElasticsearchAdapter> {
    @Inject
    public V20200730000000_AddGl2MessageIdFieldAliasForEventsElasticsearchAdapterProvider(
            @ElasticsearchVersion Version version,
            Map<Version, Provider<V20200730000000_AddGl2MessageIdFieldAliasForEvents.ElasticsearchAdapter>> pluginBindings) {
        super(version, pluginBindings);
    }
}
