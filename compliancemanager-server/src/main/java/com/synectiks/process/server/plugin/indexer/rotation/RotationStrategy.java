/*
 * */
package com.synectiks.process.server.plugin.indexer.rotation;

import com.synectiks.process.server.indexer.IndexSet;

public interface RotationStrategy {
    void rotate(IndexSet indexSet);

    Class<? extends RotationStrategyConfig> configurationClass();

    RotationStrategyConfig defaultConfiguration();
}
