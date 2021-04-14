/*
 * */
package com.synectiks.process.server.indexer.fieldtypes;

import com.synectiks.process.server.plugin.inject.Graylog2Module;

public class FieldTypesModule extends Graylog2Module {
    @Override
    protected void configure() {
        bind(FieldTypeMapper.class).asEagerSingleton();
        bind(FieldTypeLookup.class).to(MongoFieldTypeLookup.class);
    }
}
