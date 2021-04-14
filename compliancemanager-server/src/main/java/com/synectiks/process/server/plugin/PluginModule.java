/*
 * */
package com.synectiks.process.server.plugin;

import com.google.common.util.concurrent.Service;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.synectiks.process.common.events.fields.providers.FieldValueProvider;
import com.synectiks.process.common.events.notifications.EventNotification;
import com.synectiks.process.common.events.notifications.EventNotificationConfig;
import com.synectiks.process.common.events.processor.EventProcessor;
import com.synectiks.process.common.events.processor.EventProcessorConfig;
import com.synectiks.process.common.events.processor.EventProcessorParameters;
import com.synectiks.process.common.events.processor.storage.EventStorageHandler;
import com.synectiks.process.common.grn.GRNDescriptorProvider;
import com.synectiks.process.common.grn.GRNType;
import com.synectiks.process.common.scheduler.Job;
import com.synectiks.process.common.scheduler.JobDefinitionConfig;
import com.synectiks.process.common.scheduler.JobSchedule;
import com.synectiks.process.common.scheduler.JobTriggerData;
import com.synectiks.process.common.security.authservice.AuthServiceBackend;
import com.synectiks.process.common.security.authservice.AuthServiceBackendConfig;
import com.synectiks.process.server.audit.AuditEventType;
import com.synectiks.process.server.audit.PluginAuditEventTypes;
import com.synectiks.process.server.audit.formatter.AuditEventFormatter;
import com.synectiks.process.server.contentpacks.constraints.ConstraintChecker;
import com.synectiks.process.server.contentpacks.facades.EntityFacade;
import com.synectiks.process.server.contentpacks.model.ModelType;
import com.synectiks.process.server.migrations.Migration;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.alarms.callbacks.AlarmCallback;
import com.synectiks.process.server.plugin.filters.MessageFilter;
import com.synectiks.process.server.plugin.indexer.retention.RetentionStrategy;
import com.synectiks.process.server.plugin.indexer.rotation.RotationStrategy;
import com.synectiks.process.server.plugin.inject.Graylog2Module;
import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.codecs.Codec;
import com.synectiks.process.server.plugin.inputs.transports.Transport;
import com.synectiks.process.server.plugin.messageprocessors.MessageProcessor;
import com.synectiks.process.server.plugin.outputs.MessageOutput;
import com.synectiks.process.server.plugin.periodical.Periodical;
import com.synectiks.process.server.plugin.rest.PluginRestResource;
import com.synectiks.process.server.plugin.security.PasswordAlgorithm;
import com.synectiks.process.server.plugin.security.PluginPermissions;

import javax.ws.rs.ext.ExceptionMapper;
import java.util.Collections;
import java.util.Set;

public abstract class PluginModule extends Graylog2Module {
    public Set<? extends PluginConfigBean> getConfigBeans() {
        return Collections.emptySet();
    }

    protected void addMessageInput(Class<? extends MessageInput> messageInputClass) {
        installInput(inputsMapBinder(), messageInputClass);
    }

    protected <T extends MessageInput> void addMessageInput(Class<T> messageInputClass,
                                                            Class<? extends MessageInput.Factory<T>> factoryClass) {
        installInput(inputsMapBinder(), messageInputClass, factoryClass);
    }

    protected void addMessageFilter(Class<? extends MessageFilter> messageFilterClass) {
        Multibinder<MessageFilter> messageInputs = Multibinder.newSetBinder(binder(), MessageFilter.class);
        messageInputs.addBinding().to(messageFilterClass);
    }

    protected void addPeriodical(Class<? extends Periodical> periodicalClass) {
        Multibinder<Periodical> periodicalBinder = Multibinder.newSetBinder(binder(), Periodical.class);
        periodicalBinder.addBinding().to(periodicalClass);
    }

    protected void addRotationStrategy(Class<? extends RotationStrategy> rotationStrategyClass) {
        installRotationStrategy(rotationStrategiesMapBinder(), rotationStrategyClass);
    }

    protected void addRetentionStrategy(Class<? extends RetentionStrategy> retentionStrategyClass) {
        installRetentionStrategy(retentionStrategyMapBinder(), retentionStrategyClass);
    }

    protected void addAlarmCallback(Class<? extends AlarmCallback> alarmCallbackClass) {
        Multibinder<AlarmCallback> alarmCallbackInstanceBinder = Multibinder.newSetBinder(binder(), AlarmCallback.class);
        alarmCallbackInstanceBinder.addBinding().to(alarmCallbackClass);

        TypeLiteral<Class<? extends AlarmCallback>> type = new TypeLiteral<Class<? extends AlarmCallback>>() {
        };
        Multibinder<Class<? extends AlarmCallback>> alarmCallbackBinder = Multibinder.newSetBinder(binder(), type);
        alarmCallbackBinder.addBinding().toInstance(alarmCallbackClass);
    }

    protected void addInitializer(Class<? extends Service> initializerClass) {
        Multibinder<Service> serviceBinder = serviceBinder();
        serviceBinder.addBinding().to(initializerClass);
    }

    // This should only be used by plugins that have been built before alertmanager 3.0.1.
    // See comments in MessageOutput.Factory and MessageOutput.Factory2 for details
    protected void addMessageOutput(Class<? extends MessageOutput> messageOutputClass) {
        installOutput(outputsMapBinder(), messageOutputClass);
    }

    // This should only be used by plugins that have been built before alertmanager 3.0.1.
    // See comments in MessageOutput.Factory and MessageOutput.Factory2 for details
    protected <T extends MessageOutput> void addMessageOutput(Class<T> messageOutputClass,
                                                              Class<? extends MessageOutput.Factory<T>> factory) {
        installOutput(outputsMapBinder(), messageOutputClass, factory);
    }

    // This should be used by plugins that have been built for 3.0.1 or later.
    // See comments in MessageOutput.Factory and MessageOutput.Factory2 for details
    protected <T extends MessageOutput> void addMessageOutput2(Class<T> messageOutputClass,
                                                              Class<? extends MessageOutput.Factory2<T>> factory) {
        installOutput2(outputsMapBinder2(), messageOutputClass, factory);
    }

    protected void addRestResource(Class<? extends PluginRestResource> restResourceClass) {
        MapBinder<String, Class<? extends PluginRestResource>> pluginRestResourceMapBinder =
                MapBinder.newMapBinder(binder(), new TypeLiteral<String>() {},
                                       new TypeLiteral<Class<? extends PluginRestResource>>() {})
                        .permitDuplicates();
        pluginRestResourceMapBinder.addBinding(this.getClass().getPackage().getName()).toInstance(restResourceClass);
    }

    protected void addJerseyExceptionMapper(Class<? extends ExceptionMapper> exceptionMapperClass) {
        jerseyExceptionMapperBinder().addBinding().toInstance(exceptionMapperClass);
    }

    protected void addConfigBeans() {
        final Multibinder<PluginConfigBean> pluginConfigBeans = Multibinder.newSetBinder(binder(), PluginConfigBean.class);
        for (PluginConfigBean pluginConfigBean : getConfigBeans()) {
            pluginConfigBeans.addBinding().toInstance(pluginConfigBean);
        }
    }

    protected void addTransport(String name, Class<? extends Transport> transportClass) {
        installTransport(transportMapBinder(), name, transportClass);
    }

    protected void addTransport(String name,
                                Class<? extends Transport> transportClass,
                                Class<? extends Transport.Config> configClass,
                                Class<? extends Transport.Factory<? extends Transport>> factoryClass) {
        installTransport(transportMapBinder(), name, transportClass, configClass, factoryClass);
    }

    protected void addCodec(String name, Class<? extends Codec> codecClass) {
        installCodec(codecMapBinder(), name, codecClass);
    }

    protected void addCodec(String name,
                            Class<? extends Codec> codecClass,
                            Class<? extends Codec.Config> configClass,
                            Class<? extends Codec.Factory<? extends Codec>> factoryClass) {
        installCodec(codecMapBinder(), name, codecClass, configClass, factoryClass);
    }

    protected void addPasswordAlgorithm(String passwordAlgorithmName, Class<? extends PasswordAlgorithm> passwordAlgorithmClass) {
        passwordAlgorithmBinder().addBinding(passwordAlgorithmName).to(passwordAlgorithmClass);
    }

    protected Multibinder<MessageProcessor> processorBinder() {
        return Multibinder.newSetBinder(binder(), MessageProcessor.class);
    }

    protected Multibinder<MessageProcessor.Descriptor> processorDescriptorBinder() {
        return Multibinder.newSetBinder(binder(), MessageProcessor.Descriptor.class);
    }

    protected void addMessageProcessor(Class<? extends MessageProcessor> processorClass, Class<? extends MessageProcessor.Descriptor> descriptorClass) {
        processorBinder().addBinding().to(processorClass);
        processorDescriptorBinder().addBinding().to(descriptorClass);
    }

    protected void addPermissions(Class<? extends PluginPermissions> permissionsClass) {
        installPermissions(permissionsBinder(), permissionsClass);
    }

    protected void addAuditEventTypes(Class<? extends PluginAuditEventTypes> auditEventTypesClass) {
        installAuditEventTypes(auditEventTypesBinder(), auditEventTypesClass);
    }

    protected void addAuditEventFormatter(AuditEventType auditEventType, Class<? extends AuditEventFormatter> auditEventFormatterClass) {
        installAuditEventFormatter(auditEventFormatterMapBinder(), auditEventType, auditEventFormatterClass);
    }

    protected void addAlertCondition(String name,
                                     Class<? extends AlertCondition> alertConditionClass,
                                     Class<? extends AlertCondition.Factory> alertConditionFactoryClass) {
        installAlertConditionWithCustomName(alertConditionBinder(), name, alertConditionClass, alertConditionFactoryClass);
    }

    protected void addMigration(Class<? extends Migration> migrationClass) {
        migrationsBinder().addBinding().to(migrationClass);
    }

    protected void addEntityFacade(ModelType entityType, Class<? extends EntityFacade<?>> entityFacadeClass) {
        entityFacadeBinder().addBinding(entityType).to(entityFacadeClass);
    }

    protected void addConstraintChecker(Class<? extends ConstraintChecker> constraintCheckerClass) {
        constraintCheckerBinder().addBinding().to(constraintCheckerClass);
    }

    private MapBinder<String, EventProcessor.Factory> eventProcessorBinder() {
        return MapBinder.newMapBinder(binder(), String.class, EventProcessor.Factory.class);
    }

    protected void addEventProcessor(String name,
                                     Class<? extends EventProcessor> processorClass,
                                     Class<? extends EventProcessor.Factory> factoryClass,
                                     Class<? extends EventProcessorConfig> configClass,
                                     Class<? extends EventProcessorParameters> parametersClass) {
        install(new FactoryModuleBuilder().implement(EventProcessor.class, processorClass).build(factoryClass));
        eventProcessorBinder().addBinding(name).to(factoryClass);
        registerJacksonSubtype(configClass, name);
        registerJacksonSubtype(parametersClass, name);
    }

    private MapBinder<String, EventStorageHandler.Factory> eventStorageHandlerBinder() {
        return MapBinder.newMapBinder(binder(), String.class, EventStorageHandler.Factory.class);
    }

    protected void addEventStorageHandler(String name,
                                          Class<? extends EventStorageHandler> handlerClass,
                                          Class<? extends EventStorageHandler.Factory> factoryClass,
                                          Class<? extends EventStorageHandler.Config> configClass) {
        install(new FactoryModuleBuilder().implement(EventStorageHandler.class, handlerClass).build(factoryClass));
        eventStorageHandlerBinder().addBinding(name).to(factoryClass);
        registerJacksonSubtype(configClass, name);
    }

    private MapBinder<String, FieldValueProvider.Factory> eventFieldValueProviderBinder() {
        return MapBinder.newMapBinder(binder(), String.class, FieldValueProvider.Factory.class);
    }

    protected void addEventFieldValueProvider(String name,
                                              Class<? extends FieldValueProvider> fieldValueProviderClass,
                                              Class<? extends FieldValueProvider.Factory> factoryClass,
                                              Class<? extends FieldValueProvider.Config> configClass) {
        install(new FactoryModuleBuilder().implement(FieldValueProvider.class, fieldValueProviderClass).build(factoryClass));
        eventFieldValueProviderBinder().addBinding(name).to(factoryClass);
        registerJacksonSubtype(configClass, name);
    }

    private MapBinder<String, Job.Factory> jobBinder() {
        return MapBinder.newMapBinder(binder(), String.class, Job.Factory.class);
    }

    protected void addSchedulerJob(String name,
                                 Class<? extends Job> jobClass,
                                 Class<? extends Job.Factory> factoryClass,
                                 Class<? extends JobDefinitionConfig> configClass) {
        addSchedulerJob(name, jobClass, factoryClass, configClass, null);
    }

    protected void addSchedulerJob(String name,
                                   Class<? extends Job> jobClass,
                                   Class<? extends Job.Factory> factoryClass,
                                   Class<? extends JobDefinitionConfig> configClass,
                                   Class<? extends JobTriggerData> dataClass) {
        install(new FactoryModuleBuilder().implement(Job.class, jobClass).build(factoryClass));
        jobBinder().addBinding(name).to(factoryClass);
        registerJacksonSubtype(configClass, name);

        // Some jobs might not have a custom data class
        if (dataClass != null) {
            registerJacksonSubtype(dataClass, name);
        }
    }

    protected void addJobSchedulerSchedule(String name, Class<? extends JobSchedule> scheduleClass) {
        registerJacksonSubtype(scheduleClass, name);
    }

    private MapBinder<String, EventNotification.Factory> eventNotificationBinder() {
        return MapBinder.newMapBinder(binder(), String.class, EventNotification.Factory.class);
    }

    protected void addNotificationType(String name,
                                       Class<? extends EventNotificationConfig> notificationClass,
                                       Class<? extends EventNotification> handlerClass,
                                       Class<? extends EventNotification.Factory> factoryClass) {
        install(new FactoryModuleBuilder().implement(EventNotification.class, handlerClass).build(factoryClass));
        eventNotificationBinder().addBinding(name).to(factoryClass);
        registerJacksonSubtype(notificationClass, name);
    }

    protected void addGRNType(GRNType type, Class<? extends GRNDescriptorProvider> descriptorProvider) {
        final MapBinder<GRNType, GRNDescriptorProvider> mapBinder = MapBinder.newMapBinder(binder(), GRNType.class, GRNDescriptorProvider.class);
        mapBinder.addBinding(type).to(descriptorProvider);
    }

    protected MapBinder<String, AuthServiceBackend.Factory<? extends AuthServiceBackend>> authServiceBackendBinder() {
        return MapBinder.newMapBinder(
                binder(),
                TypeLiteral.get(String.class),
                new TypeLiteral<AuthServiceBackend.Factory<? extends AuthServiceBackend>>() {}
        );
    }

    protected void addAuthServiceBackend(String name,
            Class<? extends AuthServiceBackend> backendClass,
            Class<? extends AuthServiceBackend.Factory<? extends AuthServiceBackend>> factoryClass,
            Class<? extends AuthServiceBackendConfig> configClass) {
        install(new FactoryModuleBuilder().implement(AuthServiceBackend.class, backendClass).build(factoryClass));
        authServiceBackendBinder().addBinding(name).to(factoryClass);
        registerJacksonSubtype(configClass, name);
    }
}
