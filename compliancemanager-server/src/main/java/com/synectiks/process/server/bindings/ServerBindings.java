/*
 * */
package com.synectiks.process.server.bindings;

import com.floreysoft.jmte.Engine;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.alerts.AlertSender;
import com.synectiks.process.server.alerts.EmailRecipients;
import com.synectiks.process.server.alerts.FormattedEmailAlertSender;
import com.synectiks.process.server.bindings.providers.ClusterEventBusProvider;
import com.synectiks.process.server.bindings.providers.DefaultSecurityManagerProvider;
import com.synectiks.process.server.bindings.providers.DefaultStreamProvider;
import com.synectiks.process.server.bindings.providers.MongoConnectionProvider;
import com.synectiks.process.server.bindings.providers.SystemJobFactoryProvider;
import com.synectiks.process.server.bindings.providers.SystemJobManagerProvider;
import com.synectiks.process.server.cluster.ClusterConfigServiceImpl;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.events.ClusterEventBus;
import com.synectiks.process.server.grok.GrokModule;
import com.synectiks.process.server.grok.GrokPatternRegistry;
import com.synectiks.process.server.indexer.SetIndexReadOnlyJob;
import com.synectiks.process.server.indexer.fieldtypes.FieldTypesModule;
import com.synectiks.process.server.indexer.healing.FixDeflectorByDeleteJob;
import com.synectiks.process.server.indexer.healing.FixDeflectorByMoveJob;
import com.synectiks.process.server.indexer.indices.jobs.IndexSetCleanupJob;
import com.synectiks.process.server.indexer.indices.jobs.OptimizeIndexJob;
import com.synectiks.process.server.indexer.indices.jobs.SetIndexReadOnlyAndCalculateRangeJob;
import com.synectiks.process.server.indexer.ranges.CreateNewSingleIndexRangeJob;
import com.synectiks.process.server.indexer.ranges.RebuildIndexRangesJob;
import com.synectiks.process.server.inputs.InputEventListener;
import com.synectiks.process.server.inputs.InputStateListener;
import com.synectiks.process.server.inputs.PersistedInputsImpl;
import com.synectiks.process.server.lookup.LookupModule;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;
import com.synectiks.process.server.plugin.inject.Graylog2Module;
import com.synectiks.process.server.plugin.rest.ValidationFailureExceptionMapper;
import com.synectiks.process.server.plugin.streams.DefaultStream;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.rest.ElasticsearchExceptionMapper;
import com.synectiks.process.server.rest.GenericErrorCsvWriter;
import com.synectiks.process.server.rest.GraylogErrorPageGenerator;
import com.synectiks.process.server.rest.NotFoundExceptionMapper;
import com.synectiks.process.server.rest.QueryParsingExceptionMapper;
import com.synectiks.process.server.rest.ScrollChunkWriter;
import com.synectiks.process.server.rest.ValidationExceptionMapper;
import com.synectiks.process.server.security.realm.AuthenticatingRealmModule;
import com.synectiks.process.server.security.realm.AuthorizationOnlyRealmModule;
import com.synectiks.process.server.shared.buffers.processors.ProcessBufferProcessor;
import com.synectiks.process.server.shared.inputs.PersistedInputs;
import com.synectiks.process.server.shared.journal.JournalReaderModule;
import com.synectiks.process.server.shared.journal.KafkaJournalModule;
import com.synectiks.process.server.shared.journal.NoopJournalModule;
import com.synectiks.process.server.shared.metrics.jersey2.MetricsDynamicBinding;
import com.synectiks.process.server.shared.security.RestrictToMasterFeature;
import com.synectiks.process.server.shared.system.activities.ActivityWriter;
import com.synectiks.process.server.streams.DefaultStreamChangeHandler;
import com.synectiks.process.server.streams.StreamRouter;
import com.synectiks.process.server.streams.StreamRouterEngine;
import com.synectiks.process.server.system.activities.SystemMessageActivityWriter;
import com.synectiks.process.server.system.debug.ClusterDebugEventListener;
import com.synectiks.process.server.system.debug.LocalDebugEventListener;
import com.synectiks.process.server.system.jobs.SystemJobFactory;
import com.synectiks.process.server.system.jobs.SystemJobManager;
import com.synectiks.process.server.system.shutdown.GracefulShutdown;
import com.synectiks.process.server.system.stats.ClusterStatsModule;
import com.synectiks.process.server.users.RoleService;
import com.synectiks.process.server.users.RoleServiceImpl;
import com.synectiks.process.server.users.StartPageCleanupListener;
import com.synectiks.process.server.users.UserImpl;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.glassfish.grizzly.http.server.ErrorPageGenerator;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.ext.ExceptionMapper;

public class ServerBindings extends Graylog2Module {
    private final Configuration configuration;

    public ServerBindings(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        bindInterfaces();
        bindSingletons();
        bindProviders();
        bindFactoryModules();
        bindDynamicFeatures();
        bindExceptionMappers();
        bindAdditionalJerseyComponents();
        bindEventBusListeners();
        install(new AuthenticatingRealmModule(configuration));
        install(new AuthorizationOnlyRealmModule());
        bindSearchResponseDecorators();
        install(new GrokModule());
        install(new LookupModule());
        install(new FieldTypesModule());

        // Just to create the binders so they are present in the injector. Prevents a server startup error when no
        // outputs are bound that implement MessageOutput.Factory2.
        outputsMapBinder2();
    }

    private void bindProviders() {
        bind(ClusterEventBus.class).toProvider(ClusterEventBusProvider.class).asEagerSingleton();
    }

    private void bindFactoryModules() {
        // System Jobs
        install(new FactoryModuleBuilder().build(RebuildIndexRangesJob.Factory.class));
        install(new FactoryModuleBuilder().build(OptimizeIndexJob.Factory.class));
        install(new FactoryModuleBuilder().build(SetIndexReadOnlyJob.Factory.class));
        install(new FactoryModuleBuilder().build(IndexSetCleanupJob.Factory.class));
        install(new FactoryModuleBuilder().build(CreateNewSingleIndexRangeJob.Factory.class));
        install(new FactoryModuleBuilder().build(FixDeflectorByDeleteJob.Factory.class));
        install(new FactoryModuleBuilder().build(FixDeflectorByMoveJob.Factory.class));
        install(new FactoryModuleBuilder().build(SetIndexReadOnlyAndCalculateRangeJob.Factory.class));

        install(new FactoryModuleBuilder().build(UserImpl.Factory.class));

        install(new FactoryModuleBuilder().build(EmailRecipients.Factory.class));

        install(new FactoryModuleBuilder().build(ProcessBufferProcessor.Factory.class));
        bind(Stream.class).annotatedWith(DefaultStream.class).toProvider(DefaultStreamProvider.class);
        bind(DefaultStreamChangeHandler.class).asEagerSingleton();
    }

    private void bindSingletons() {
        bind(MongoConnection.class).toProvider(MongoConnectionProvider.class);

        if (configuration.isMessageJournalEnabled()) {
            install(new KafkaJournalModule());
            install(new JournalReaderModule());
        } else {
            install(new NoopJournalModule());
        }

        bind(SystemJobManager.class).toProvider(SystemJobManagerProvider.class);
        bind(DefaultSecurityManager.class).toProvider(DefaultSecurityManagerProvider.class).asEagerSingleton();
        bind(SystemJobFactory.class).toProvider(SystemJobFactoryProvider.class);
        bind(GracefulShutdown.class).in(Scopes.SINGLETON);
        bind(ClusterStatsModule.class).asEagerSingleton();
        bind(ClusterConfigService.class).to(ClusterConfigServiceImpl.class).asEagerSingleton();
        bind(GrokPatternRegistry.class).in(Scopes.SINGLETON);
        bind(Engine.class).toInstance(Engine.createEngine());
        bind(ErrorPageGenerator.class).to(GraylogErrorPageGenerator.class).asEagerSingleton();

        registerRestControllerPackage("com.synectiks.process.server.rest.resources");
        registerRestControllerPackage("com.synectiks.process.server.shared.rest.resources");
        // Register Xformation rest controller
        registerRestControllerPackage("com.synectiks.process.server.xformation.rest");
    }

    private void bindInterfaces() {
        bind(AlertSender.class).to(FormattedEmailAlertSender.class);
        bind(StreamRouter.class);
        install(new FactoryModuleBuilder().implement(StreamRouterEngine.class, StreamRouterEngine.class).build(
                StreamRouterEngine.Factory.class));
        bind(ActivityWriter.class).to(SystemMessageActivityWriter.class);
        bind(PersistedInputs.class).to(PersistedInputsImpl.class);

        bind(RoleService.class).to(RoleServiceImpl.class).in(Scopes.SINGLETON);
    }

    private void bindDynamicFeatures() {
        final Multibinder<Class<? extends DynamicFeature>> dynamicFeatures = jerseyDynamicFeatureBinder();
        dynamicFeatures.addBinding().toInstance(MetricsDynamicBinding.class);
        dynamicFeatures.addBinding().toInstance(RestrictToMasterFeature.class);
    }

    private void bindExceptionMappers() {
        final Multibinder<Class<? extends ExceptionMapper>> exceptionMappers = jerseyExceptionMapperBinder();
        exceptionMappers.addBinding().toInstance(NotFoundExceptionMapper.class);
        exceptionMappers.addBinding().toInstance(ValidationExceptionMapper.class);
        exceptionMappers.addBinding().toInstance(ValidationFailureExceptionMapper.class);
        exceptionMappers.addBinding().toInstance(ElasticsearchExceptionMapper.class);
        exceptionMappers.addBinding().toInstance(QueryParsingExceptionMapper.class);
    }

    private void bindAdditionalJerseyComponents() {
        jerseyAdditionalComponentsBinder().addBinding().toInstance(ScrollChunkWriter.class);
        jerseyAdditionalComponentsBinder().addBinding().toInstance(GenericErrorCsvWriter.class);
    }

    private void bindEventBusListeners() {
        bind(InputStateListener.class).asEagerSingleton();
        bind(InputEventListener.class).asEagerSingleton();
        bind(LocalDebugEventListener.class).asEagerSingleton();
        bind(ClusterDebugEventListener.class).asEagerSingleton();
        bind(StartPageCleanupListener.class).asEagerSingleton();
    }

    private void bindSearchResponseDecorators() {
        // only triggering an initialize to make sure that the binding exists
        searchResponseDecoratorBinder();
    }
}
