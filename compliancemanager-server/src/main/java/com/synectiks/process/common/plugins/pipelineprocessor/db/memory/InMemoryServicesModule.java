/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.db.memory;

import com.synectiks.process.common.plugins.pipelineprocessor.db.PipelineService;
import com.synectiks.process.common.plugins.pipelineprocessor.db.PipelineStreamConnectionsService;
import com.synectiks.process.common.plugins.pipelineprocessor.db.RuleService;
import com.synectiks.process.server.plugin.PluginModule;

public class InMemoryServicesModule extends PluginModule {
    @Override
    protected void configure() {
        bind(RuleService.class).to(InMemoryRuleService.class).asEagerSingleton();
        bind(PipelineService.class).to(InMemoryPipelineService.class).asEagerSingleton();
        bind(PipelineStreamConnectionsService.class).to(InMemoryPipelineStreamConnectionsService.class).asEagerSingleton();
    }
}
