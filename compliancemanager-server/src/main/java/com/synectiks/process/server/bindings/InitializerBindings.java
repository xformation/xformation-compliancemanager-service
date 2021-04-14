/*
 * */
package com.synectiks.process.server.bindings;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.synectiks.process.server.initializers.BufferSynchronizerService;
import com.synectiks.process.server.initializers.OutputSetupService;

public class InitializerBindings extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<Service> serviceBinder = Multibinder.newSetBinder(binder(), Service.class);
        serviceBinder.addBinding().to(BufferSynchronizerService.class);
        serviceBinder.addBinding().to(OutputSetupService.class);
    }
}
