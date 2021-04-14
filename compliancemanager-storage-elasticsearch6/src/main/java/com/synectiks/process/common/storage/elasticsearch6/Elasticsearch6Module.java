/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.synectiks.process.common.events.search.MoreSearchAdapter;
import com.synectiks.process.common.plugins.views.migrations.V20200730000000_AddGl2MessageIdFieldAliasForEvents;
import com.synectiks.process.common.storage.elasticsearch6.jest.JestClientProvider;
import com.synectiks.process.common.storage.elasticsearch6.migrations.V20170607164210_MigrateReopenedIndicesToAliasesClusterStateES6;
import com.synectiks.process.common.storage.elasticsearch6.views.migrations.V20200730000000_AddGl2MessageIdFieldAliasForEventsES6;
import com.synectiks.process.server.indexer.IndexToolsAdapter;
import com.synectiks.process.server.indexer.cluster.ClusterAdapter;
import com.synectiks.process.server.indexer.cluster.NodeAdapter;
import com.synectiks.process.server.indexer.counts.CountsAdapter;
import com.synectiks.process.server.indexer.fieldtypes.IndexFieldTypePollerAdapter;
import com.synectiks.process.server.indexer.indices.IndicesAdapter;
import com.synectiks.process.server.indexer.messages.MessagesAdapter;
import com.synectiks.process.server.indexer.searches.SearchesAdapter;
import com.synectiks.process.server.migrations.V20170607164210_MigrateReopenedIndicesToAliases;
import com.synectiks.process.server.plugin.VersionAwareModule;

import io.searchbox.client.JestClient;

import static com.synectiks.process.common.storage.elasticsearch6.Elasticsearch6Plugin.SUPPORTED_ES_VERSION;

public class Elasticsearch6Module extends VersionAwareModule {
    @Override
    protected void configure() {
        bindForSupportedVersion(CountsAdapter.class).to(CountsAdapterES6.class);
        bindForSupportedVersion(IndicesAdapter.class).to(IndicesAdapterES6.class);
        bindForSupportedVersion(SearchesAdapter.class).to(SearchesAdapterES6.class);
        bindForSupportedVersion(MoreSearchAdapter.class).to(MoreSearchAdapterES6.class);
        bindForSupportedVersion(MessagesAdapter.class).to(MessagesAdapterES6.class);
        bindForSupportedVersion(ClusterAdapter.class).to(ClusterAdapterES6.class);
        bindForSupportedVersion(NodeAdapter.class).to(NodeAdapterES6.class);
        bindForSupportedVersion(IndexFieldTypePollerAdapter.class).to(IndexFieldTypePollerAdapterES6.class);
        bindForSupportedVersion(IndexToolsAdapter.class).to(IndexToolsAdapterES6.class);
        bindForSupportedVersion(V20170607164210_MigrateReopenedIndicesToAliases.ClusterState.class)
                .to(V20170607164210_MigrateReopenedIndicesToAliasesClusterStateES6.class);
        bindForSupportedVersion(V20200730000000_AddGl2MessageIdFieldAliasForEvents.ElasticsearchAdapter.class)
                .to(V20200730000000_AddGl2MessageIdFieldAliasForEventsES6.class);

        install(new FactoryModuleBuilder().build(ScrollResultES6.Factory.class));

        bind(JestClient.class).toProvider(JestClientProvider.class);
    }

    private <T> LinkedBindingBuilder<T> bindForSupportedVersion(Class<T> interfaceClass) {
        return bindForVersion(SUPPORTED_ES_VERSION, interfaceClass);
    }
}
