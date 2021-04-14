/*
 * */
package com.synectiks.process.common.scheduler;

import com.google.inject.multibindings.OptionalBinder;
import com.synectiks.process.common.scheduler.audit.JobSchedulerAuditEventTypes;
import com.synectiks.process.common.scheduler.clock.JobSchedulerClock;
import com.synectiks.process.common.scheduler.clock.JobSchedulerSystemClock;
import com.synectiks.process.common.scheduler.eventbus.JobSchedulerEventBus;
import com.synectiks.process.common.scheduler.eventbus.JobSchedulerEventBusProvider;
import com.synectiks.process.server.plugin.PluginModule;

/**
 * Job scheduler specific bindings.
 */
public class JobSchedulerModule extends PluginModule {
    @Override
    protected void configure() {
        bind(JobSchedulerService.class).asEagerSingleton();
        bind(JobSchedulerClock.class).toInstance(JobSchedulerSystemClock.INSTANCE);
        bind(JobSchedulerEventBus.class).toProvider(JobSchedulerEventBusProvider.class).asEagerSingleton();

        OptionalBinder.newOptionalBinder(binder(), JobSchedulerConfig.class)
                .setDefault().to(DefaultJobSchedulerConfig.class);

        // Add all rest resources in this package
        registerRestControllerPackage(getClass().getPackage().getName());

        addInitializer(JobSchedulerService.class);
        addAuditEventTypes(JobSchedulerAuditEventTypes.class);
    }
}
