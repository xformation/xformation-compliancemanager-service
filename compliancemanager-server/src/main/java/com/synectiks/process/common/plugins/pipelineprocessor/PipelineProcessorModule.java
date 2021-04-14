/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.synectiks.process.common.plugins.pipelineprocessor.audit.PipelineProcessorAuditEventTypes;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.ProcessorFunctionsModule;
import com.synectiks.process.common.plugins.pipelineprocessor.periodical.LegacyDefaultStreamMigration;
import com.synectiks.process.common.plugins.pipelineprocessor.processors.PipelineInterpreter;
import com.synectiks.process.common.plugins.pipelineprocessor.rest.PipelineRestPermissions;
import com.synectiks.process.server.plugin.PluginModule;

public class PipelineProcessorModule extends PluginModule {
    @Override
    protected void configure() {
        addPeriodical(LegacyDefaultStreamMigration.class);

        addMessageProcessor(PipelineInterpreter.class, PipelineInterpreter.Descriptor.class);
        addPermissions(PipelineRestPermissions.class);

        registerRestControllerPackage(getClass().getPackage().getName());

        install(new ProcessorFunctionsModule());

        installSearchResponseDecorator(searchResponseDecoratorBinder(),
                PipelineProcessorMessageDecorator.class,
                PipelineProcessorMessageDecorator.Factory.class);

        install(new FactoryModuleBuilder().build(PipelineInterpreter.State.Factory.class));

        addAuditEventTypes(PipelineProcessorAuditEventTypes.class);
    }
}
