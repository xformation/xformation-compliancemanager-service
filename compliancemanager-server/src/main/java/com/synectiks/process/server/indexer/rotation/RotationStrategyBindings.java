/*
 * */
package com.synectiks.process.server.indexer.rotation;

import com.synectiks.process.server.indexer.rotation.strategies.MessageCountRotationStrategy;
import com.synectiks.process.server.indexer.rotation.strategies.SizeBasedRotationStrategy;
import com.synectiks.process.server.indexer.rotation.strategies.TimeBasedRotationStrategy;
import com.synectiks.process.server.plugin.PluginModule;

public class RotationStrategyBindings extends PluginModule {
    @Override
    protected void configure() {
        addRotationStrategy(MessageCountRotationStrategy.class);
        addRotationStrategy(SizeBasedRotationStrategy.class);
        addRotationStrategy(TimeBasedRotationStrategy.class);
    }

}