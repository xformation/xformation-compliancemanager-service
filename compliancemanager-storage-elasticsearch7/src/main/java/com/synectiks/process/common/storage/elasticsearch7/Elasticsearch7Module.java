/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.synectiks.process.common.events.search.MoreSearchAdapter;
import com.synectiks.process.common.plugins.views.migrations.V20200730000000_AddGl2MessageIdFieldAliasForEvents;
import com.synectiks.process.common.storage.elasticsearch7.client.ESCredentialsProvider;
import com.synectiks.process.common.storage.elasticsearch7.migrations.V20170607164210_MigrateReopenedIndicesToAliasesClusterStateES7;
import com.synectiks.process.common.storage.elasticsearch7.views.migrations.V20200730000000_AddGl2MessageIdFieldAliasForEventsES7;

import static com.synectiks.process.common.storage.elasticsearch7.Elasticsearch7Plugin.SUPPORTED_ES_VERSION;

import org.graylog.shaded.elasticsearch7.org.apache.http.client.CredentialsProvider;
import org.graylog.shaded.elasticsearch7.org.elasticsearch.client.RestHighLevelClient;

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

public class Elasticsearch7Module extends VersionAwareModule {
    @Override
    protected void configure() {
        bindForSupportedVersion(CountsAdapter.class).to(CountsAdapterES7.class);
        bindForSupportedVersion(ClusterAdapter.class).to(ClusterAdapterES7.class);
        bindForSupportedVersion(IndicesAdapter.class).to(IndicesAdapterES7.class);
        bindForSupportedVersion(IndexFieldTypePollerAdapter.class).to(IndexFieldTypePollerAdapterES7.class);
        bindForSupportedVersion(IndexToolsAdapter.class).to(IndexToolsAdapterES7.class);
        bindForSupportedVersion(MessagesAdapter.class).to(MessagesAdapterES7.class);
        bindForSupportedVersion(MoreSearchAdapter.class).to(MoreSearchAdapterES7.class);
        bindForSupportedVersion(NodeAdapter.class).to(NodeAdapterES7.class);
        bindForSupportedVersion(SearchesAdapter.class).to(SearchesAdapterES7.class);
        bindForSupportedVersion(V20170607164210_MigrateReopenedIndicesToAliases.ClusterState.class)
                .to(V20170607164210_MigrateReopenedIndicesToAliasesClusterStateES7.class);
        bindForSupportedVersion(V20200730000000_AddGl2MessageIdFieldAliasForEvents.ElasticsearchAdapter.class)
                .to(V20200730000000_AddGl2MessageIdFieldAliasForEventsES7.class);

        install(new FactoryModuleBuilder().build(ScrollResultES7.Factory.class));

        bind(RestHighLevelClient.class).toProvider(RestHighLevelClientProvider.class);
        bind(CredentialsProvider.class).toProvider(ESCredentialsProvider.class);
    }

    private <T> LinkedBindingBuilder<T> bindForSupportedVersion(Class<T> interfaceClass) {
        return bindForVersion(SUPPORTED_ES_VERSION, interfaceClass);
    }
}
