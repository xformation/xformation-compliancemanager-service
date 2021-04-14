/*
 * */
package com.synectiks.process.server.indexer;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.synectiks.process.server.indexer.indexset.IndexSetService;
import com.synectiks.process.server.indexer.indexset.MongoIndexSetService;
import com.synectiks.process.server.plugin.inject.Graylog2Module;

public class IndexerBindings extends Graylog2Module {
    @Override
    protected void configure() {
        bind(IndexSetService.class).to(MongoIndexSetService.class);

        install(new FactoryModuleBuilder().build(MongoIndexSet.Factory.class));
        bind(IndexSetRegistry.class).to(MongoIndexSetRegistry.class);
    }
}
