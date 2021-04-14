/*
 * */
package com.synectiks.process.common.events;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.synectiks.process.common.events.audit.EventsAuditEventTypes;
import com.synectiks.process.common.events.contentpack.entities.AggregationEventProcessorConfigEntity;
import com.synectiks.process.common.events.contentpack.entities.EmailEventNotificationConfigEntity;
import com.synectiks.process.common.events.contentpack.entities.HttpEventNotificationConfigEntity;
import com.synectiks.process.common.events.contentpack.entities.LegacyAlarmCallbackEventNotificationConfigEntity;
import com.synectiks.process.common.events.contentpack.facade.EventDefinitionFacade;
import com.synectiks.process.common.events.contentpack.facade.NotificationFacade;
import com.synectiks.process.common.events.fields.EventFieldSpecEngine;
import com.synectiks.process.common.events.fields.providers.LookupTableFieldValueProvider;
import com.synectiks.process.common.events.fields.providers.TemplateFieldValueProvider;
import com.synectiks.process.common.events.indices.EventIndexer;
import com.synectiks.process.common.events.legacy.LegacyAlarmCallbackEventNotification;
import com.synectiks.process.common.events.legacy.LegacyAlarmCallbackEventNotificationConfig;
import com.synectiks.process.common.events.legacy.V20190722150700_LegacyAlertConditionMigration;
import com.synectiks.process.common.events.notifications.EventNotificationExecutionJob;
import com.synectiks.process.common.events.notifications.EventNotificationExecutionMetrics;
import com.synectiks.process.common.events.notifications.NotificationGracePeriodService;
import com.synectiks.process.common.events.notifications.types.EmailEventNotification;
import com.synectiks.process.common.events.notifications.types.EmailEventNotificationConfig;
import com.synectiks.process.common.events.notifications.types.HTTPEventNotification;
import com.synectiks.process.common.events.notifications.types.HTTPEventNotificationConfig;
import com.synectiks.process.common.events.periodicals.EventNotificationStatusCleanUp;
import com.synectiks.process.common.events.processor.EventProcessorEngine;
import com.synectiks.process.common.events.processor.EventProcessorExecutionJob;
import com.synectiks.process.common.events.processor.EventProcessorExecutionMetrics;
import com.synectiks.process.common.events.processor.aggregation.AggregationEventProcessor;
import com.synectiks.process.common.events.processor.aggregation.AggregationEventProcessorConfig;
import com.synectiks.process.common.events.processor.aggregation.AggregationEventProcessorParameters;
import com.synectiks.process.common.events.processor.aggregation.AggregationSearch;
import com.synectiks.process.common.events.processor.aggregation.PivotAggregationSearch;
import com.synectiks.process.common.events.processor.storage.EventStorageHandlerEngine;
import com.synectiks.process.common.events.processor.storage.PersistToStreamsStorageHandler;
import com.synectiks.process.common.scheduler.JobExecutionEngine;
import com.synectiks.process.common.scheduler.JobTriggerUpdates;
import com.synectiks.process.common.scheduler.schedule.IntervalJobSchedule;
import com.synectiks.process.common.scheduler.schedule.OnceJobSchedule;
import com.synectiks.process.common.scheduler.worker.JobWorkerPool;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.plugin.PluginConfigBean;
import com.synectiks.process.server.plugin.PluginModule;

import java.util.Collections;
import java.util.Set;

public class EventsModule extends PluginModule {
    @Override
    public Set<? extends PluginConfigBean> getConfigBeans() {
        return Collections.emptySet();
    }

    @Override
    protected void configure() {
        bind(EventProcessorEngine.class).asEagerSingleton();
        bind(EventStorageHandlerEngine.class).asEagerSingleton();
        bind(EventFieldSpecEngine.class).asEagerSingleton();
        bind(EventIndexer.class).asEagerSingleton();
        bind(NotificationGracePeriodService.class).asEagerSingleton();
        bind(EventProcessorExecutionMetrics.class).asEagerSingleton();
        bind(EventNotificationExecutionMetrics.class).asEagerSingleton();

        install(new FactoryModuleBuilder().build(JobExecutionEngine.Factory.class));
        install(new FactoryModuleBuilder().build(JobWorkerPool.Factory.class));
        install(new FactoryModuleBuilder().build(JobTriggerUpdates.Factory.class));

        // Add all rest resources in this package
        registerRestControllerPackage(getClass().getPackage().getName());

        addPeriodical(EventNotificationStatusCleanUp.class);

        addEntityFacade(ModelTypes.EVENT_DEFINITION_V1, EventDefinitionFacade.class);
        addEntityFacade(ModelTypes.NOTIFICATION_V1, NotificationFacade.class);

        addMigration(V20190722150700_LegacyAlertConditionMigration.class);
        addAuditEventTypes(EventsAuditEventTypes.class);

        registerJacksonSubtype(AggregationEventProcessorConfigEntity.class,
            AggregationEventProcessorConfigEntity.TYPE_NAME);
        registerJacksonSubtype(HttpEventNotificationConfigEntity.class,
            HttpEventNotificationConfigEntity.TYPE_NAME);
        registerJacksonSubtype(EmailEventNotificationConfigEntity.class,
            EmailEventNotificationConfigEntity.TYPE_NAME);
        registerJacksonSubtype(LegacyAlarmCallbackEventNotificationConfigEntity.class,
            LegacyAlarmCallbackEventNotificationConfigEntity.TYPE_NAME);

        addEventProcessor(AggregationEventProcessorConfig.TYPE_NAME,
                AggregationEventProcessor.class,
                AggregationEventProcessor.Factory.class,
                AggregationEventProcessorConfig.class,
                AggregationEventProcessorParameters.class);

        addEventStorageHandler(PersistToStreamsStorageHandler.Config.TYPE_NAME,
                PersistToStreamsStorageHandler.class,
                PersistToStreamsStorageHandler.Factory.class,
                PersistToStreamsStorageHandler.Config.class);

        addEventFieldValueProvider(TemplateFieldValueProvider.Config.TYPE_NAME,
                TemplateFieldValueProvider.class,
                TemplateFieldValueProvider.Factory.class,
                TemplateFieldValueProvider.Config.class);
        addEventFieldValueProvider(LookupTableFieldValueProvider.Config.TYPE_NAME,
                LookupTableFieldValueProvider.class,
                LookupTableFieldValueProvider.Factory.class,
                LookupTableFieldValueProvider.Config.class);

        addSchedulerJob(EventProcessorExecutionJob.TYPE_NAME,
                EventProcessorExecutionJob.class,
                EventProcessorExecutionJob.Factory.class,
                EventProcessorExecutionJob.Config.class,
                EventProcessorExecutionJob.Data.class);
        addSchedulerJob(EventNotificationExecutionJob.TYPE_NAME,
                EventNotificationExecutionJob.class,
                EventNotificationExecutionJob.Factory.class,
                EventNotificationExecutionJob.Config.class,
                EventNotificationExecutionJob.Data.class);

        addNotificationType(EmailEventNotificationConfig.TYPE_NAME,
                EmailEventNotificationConfig.class,
                EmailEventNotification.class,
                EmailEventNotification.Factory.class);
        addNotificationType(HTTPEventNotificationConfig.TYPE_NAME,
                HTTPEventNotificationConfig.class,
                HTTPEventNotification.class,
                HTTPEventNotification.Factory.class);
        addNotificationType(LegacyAlarmCallbackEventNotificationConfig.TYPE_NAME,
                LegacyAlarmCallbackEventNotificationConfig.class,
                LegacyAlarmCallbackEventNotification.class,
                LegacyAlarmCallbackEventNotification.Factory.class);

        addJobSchedulerSchedule(IntervalJobSchedule.TYPE_NAME, IntervalJobSchedule.class);
        addJobSchedulerSchedule(OnceJobSchedule.TYPE_NAME, OnceJobSchedule.class);

        // Change this if another aggregation search implementation should be used
        install(new FactoryModuleBuilder().implement(AggregationSearch.class, PivotAggregationSearch.class).build(AggregationSearch.Factory.class));
    }
}
