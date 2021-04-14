/*
 * */
package com.synectiks.process.server.plugin.indexer.retention;

import com.synectiks.process.server.indexer.IndexSet;

public interface RetentionStrategy {
    void retain(IndexSet indexSet);

    Class<? extends RetentionStrategyConfig> configurationClass();

    RetentionStrategyConfig defaultConfiguration();
}
