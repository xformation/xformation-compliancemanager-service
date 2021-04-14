/*
 * */
package com.synectiks.process.server.bindings;

import com.google.inject.AbstractModule;
import com.synectiks.process.server.indexer.IndexMappingFactory;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.providers.ElasticsearchVersionProvider;

public class ElasticsearchModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Version.class).annotatedWith(ElasticsearchVersion.class).toProvider(ElasticsearchVersionProvider.class).asEagerSingleton();
        bind(IndexMappingFactory.class).asEagerSingleton();
    }
}
