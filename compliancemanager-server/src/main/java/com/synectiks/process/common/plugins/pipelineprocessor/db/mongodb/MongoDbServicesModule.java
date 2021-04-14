/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.db.mongodb;

import com.synectiks.process.common.plugins.pipelineprocessor.db.PipelineService;
import com.synectiks.process.common.plugins.pipelineprocessor.db.PipelineStreamConnectionsService;
import com.synectiks.process.common.plugins.pipelineprocessor.db.RuleService;
import com.synectiks.process.server.plugin.PluginModule;

public class MongoDbServicesModule extends PluginModule {
    @Override
    protected void configure() {
        bind(PipelineService.class).to(MongoDbPipelineService.class);
        bind(RuleService.class).to(MongoDbRuleService.class);
        bind(PipelineStreamConnectionsService.class).to(MongoDbPipelineStreamConnectionsService.class);
    }
}
