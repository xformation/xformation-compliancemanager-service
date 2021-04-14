/*
 * */
package com.synectiks.process.server.shared.bindings;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.synectiks.process.server.shared.initializers.InputSetupService;
import com.synectiks.process.server.shared.initializers.JerseyService;
import com.synectiks.process.server.shared.initializers.PeriodicalsService;
import com.synectiks.process.server.system.processing.MongoDBProcessingStatusRecorderService;
import com.synectiks.process.server.system.processing.ProcessingStatusRecorder;
import com.synectiks.process.server.system.shutdown.GracefulShutdownService;

public class GenericInitializerBindings extends AbstractModule {
    @Override
    protected void configure() {
        bind(ProcessingStatusRecorder.class).to(MongoDBProcessingStatusRecorderService.class).asEagerSingleton();

        Multibinder<Service> serviceBinder = Multibinder.newSetBinder(binder(), Service.class);
        serviceBinder.addBinding().to(InputSetupService.class);
        serviceBinder.addBinding().to(PeriodicalsService.class);
        serviceBinder.addBinding().to(JerseyService.class);
        serviceBinder.addBinding().to(GracefulShutdownService.class).asEagerSingleton();
        serviceBinder.addBinding().to(MongoDBProcessingStatusRecorderService.class).asEagerSingleton();
    }
}
