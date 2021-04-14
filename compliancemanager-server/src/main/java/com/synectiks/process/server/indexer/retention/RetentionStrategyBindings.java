/*
 * */
package com.synectiks.process.server.indexer.retention;

import com.synectiks.process.server.indexer.retention.strategies.ClosingRetentionStrategy;
import com.synectiks.process.server.indexer.retention.strategies.DeletionRetentionStrategy;
import com.synectiks.process.server.indexer.retention.strategies.NoopRetentionStrategy;
import com.synectiks.process.server.plugin.PluginModule;

public class RetentionStrategyBindings extends PluginModule {
    @Override
    protected void configure() {
        addRetentionStrategy(DeletionRetentionStrategy.class);
        addRetentionStrategy(ClosingRetentionStrategy.class);
        addRetentionStrategy(NoopRetentionStrategy.class);
    }
}
