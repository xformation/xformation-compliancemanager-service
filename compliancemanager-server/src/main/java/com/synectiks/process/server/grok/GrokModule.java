/*
 * */
package com.synectiks.process.server.grok;

import com.google.inject.AbstractModule;

public class GrokModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GrokPatternService.class).to(MongoDbGrokPatternService.class);
    }
}
