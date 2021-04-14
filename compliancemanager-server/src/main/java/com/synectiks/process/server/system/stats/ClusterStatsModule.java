/*
 * */
package com.synectiks.process.server.system.stats;

import com.google.inject.AbstractModule;
import com.synectiks.process.server.system.stats.mongo.MongoProbe;

public class ClusterStatsModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MongoProbe.class).asEagerSingleton();

        bind(ClusterStatsService.class).asEagerSingleton();
    }
}
