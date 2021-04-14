/*
 * */
package com.synectiks.process.common.plugins.cef;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.synectiks.process.common.plugins.cef.codec.CEFCodec;
import com.synectiks.process.common.plugins.cef.input.CEFAmqpInput;
import com.synectiks.process.common.plugins.cef.input.CEFKafkaInput;
import com.synectiks.process.common.plugins.cef.input.CEFTCPInput;
import com.synectiks.process.common.plugins.cef.input.CEFUDPInput;
import com.synectiks.process.common.plugins.cef.pipelines.rules.CEFParserFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.Function;
import com.synectiks.process.server.plugin.PluginModule;

public class CEFInputModule extends PluginModule {
    @Override
    protected void configure() {
        // Register message input.
        addCodec(CEFCodec.NAME, CEFCodec.class);

        addMessageInput(CEFUDPInput.class);
        addMessageInput(CEFTCPInput.class);

        addMessageInput(CEFAmqpInput.class);
        addMessageInput(CEFKafkaInput.class);

        // Register pipeline function.
        addMessageProcessorFunction(CEFParserFunction.NAME, CEFParserFunction.class);
    }

    private void addMessageProcessorFunction(String name, Class<? extends Function<?>> functionClass) {
        addMessageProcessorFunction(binder(), name, functionClass);
    }

    private MapBinder<String, Function<?>> processorFunctionBinder(Binder binder) {
        return MapBinder.newMapBinder(binder, TypeLiteral.get(String.class), new TypeLiteral<Function<?>>() {});
    }

    private void addMessageProcessorFunction(Binder binder, String name, Class<? extends Function<?>> functionClass) {
        processorFunctionBinder(binder).addBinding(name).to(functionClass);
    }
}
