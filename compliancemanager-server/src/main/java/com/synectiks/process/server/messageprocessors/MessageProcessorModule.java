/*
 * */
package com.synectiks.process.server.messageprocessors;

import com.google.inject.Scopes;
import com.synectiks.process.common.plugins.pipelineprocessor.PipelineProcessorModule;
import com.synectiks.process.common.plugins.pipelineprocessor.db.mongodb.MongoDbServicesModule;
import com.synectiks.process.server.plugin.PluginModule;

public class MessageProcessorModule extends PluginModule {
    @Override
    protected void configure() {
        addMessageProcessor(MessageFilterChainProcessor.class, MessageFilterChainProcessor.Descriptor.class);
        // must not be a singleton, because each thread should get an isolated copy of the processors
        bind(OrderedMessageProcessors.class).in(Scopes.NO_SCOPE);

        install(new PipelineProcessorModule());
        install(new MongoDbServicesModule());
    }
}
