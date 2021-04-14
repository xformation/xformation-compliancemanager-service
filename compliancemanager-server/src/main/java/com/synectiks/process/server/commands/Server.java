/*
 * */
package com.synectiks.process.server.commands;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.spi.Message;
import com.mongodb.MongoException;
import com.synectiks.process.common.events.EventsModule;
import com.synectiks.process.common.freeenterprise.FreeEnterpriseConfiguration;
import com.synectiks.process.common.freeenterprise.FreeEnterpriseModule;
import com.synectiks.process.common.grn.GRNTypesModule;
import com.synectiks.process.common.plugins.cef.CEFInputModule;
import com.synectiks.process.common.plugins.map.MapWidgetModule;
import com.synectiks.process.common.plugins.netflow.NetFlowPluginModule;
import com.synectiks.process.common.plugins.pipelineprocessor.PipelineConfig;
import com.synectiks.process.common.plugins.sidecar.SidecarModule;
import com.synectiks.process.common.plugins.views.ViewsBindings;
import com.synectiks.process.common.plugins.views.ViewsConfig;
import com.synectiks.process.common.scheduler.JobSchedulerConfiguration;
import com.synectiks.process.common.scheduler.JobSchedulerModule;
import com.synectiks.process.common.security.SecurityModule;
import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.alerts.AlertConditionBindings;
import com.synectiks.process.server.audit.AuditActor;
import com.synectiks.process.server.audit.AuditBindings;
import com.synectiks.process.server.audit.AuditEventSender;
import com.synectiks.process.server.bindings.AlarmCallbackBindings;
import com.synectiks.process.server.bindings.ConfigurationModule;
import com.synectiks.process.server.bindings.ElasticsearchModule;
import com.synectiks.process.server.bindings.InitializerBindings;
import com.synectiks.process.server.bindings.MessageFilterBindings;
import com.synectiks.process.server.bindings.MessageOutputBindings;
import com.synectiks.process.server.bindings.PasswordAlgorithmBindings;
import com.synectiks.process.server.bindings.PeriodicalBindings;
import com.synectiks.process.server.bindings.PersistenceServicesBindings;
import com.synectiks.process.server.bindings.ServerBindings;
import com.synectiks.process.server.bootstrap.Main;
import com.synectiks.process.server.bootstrap.ServerBootstrap;
import com.synectiks.process.server.cluster.NodeService;
import com.synectiks.process.server.configuration.ElasticsearchClientConfiguration;
import com.synectiks.process.server.configuration.ElasticsearchConfiguration;
import com.synectiks.process.server.configuration.EmailConfiguration;
import com.synectiks.process.server.configuration.HttpConfiguration;
import com.synectiks.process.server.configuration.MongoDbConfiguration;
import com.synectiks.process.server.configuration.VersionCheckConfiguration;
import com.synectiks.process.server.contentpacks.ContentPacksModule;
import com.synectiks.process.server.decorators.DecoratorBindings;
import com.synectiks.process.server.indexer.IndexerBindings;
import com.synectiks.process.server.indexer.retention.RetentionStrategyBindings;
import com.synectiks.process.server.indexer.rotation.RotationStrategyBindings;
import com.synectiks.process.server.inputs.transports.NettyTransportConfiguration;
import com.synectiks.process.server.messageprocessors.MessageProcessorModule;
import com.synectiks.process.server.migrations.MigrationsModule;
import com.synectiks.process.server.notifications.Notification;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.plugin.KafkaJournalConfiguration;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.shared.UI;
import com.synectiks.process.server.shared.bindings.MessageInputBindings;
import com.synectiks.process.server.shared.bindings.ObjectMapperModule;
import com.synectiks.process.server.shared.bindings.RestApiBindings;
import com.synectiks.process.server.shared.system.activities.Activity;
import com.synectiks.process.server.shared.system.activities.ActivityWriter;
import com.synectiks.process.server.storage.VersionAwareStorageModule;
import com.synectiks.process.server.system.processing.ProcessingStatusConfig;
import com.synectiks.process.server.system.shutdown.GracefulShutdown;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static com.synectiks.process.server.audit.AuditEventTypes.NODE_SHUTDOWN_INITIATE;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Command(name = "server", description = "Start the alertmanager server")
public class Server extends ServerBootstrap {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    private static final Configuration configuration = new Configuration();
    private final HttpConfiguration httpConfiguration = new HttpConfiguration();
    private final ElasticsearchConfiguration elasticsearchConfiguration = new ElasticsearchConfiguration();
    private final ElasticsearchClientConfiguration elasticsearchClientConfiguration = new ElasticsearchClientConfiguration();
    private final EmailConfiguration emailConfiguration = new EmailConfiguration();
    private final MongoDbConfiguration mongoDbConfiguration = new MongoDbConfiguration();
    private final VersionCheckConfiguration versionCheckConfiguration = new VersionCheckConfiguration();
    private final KafkaJournalConfiguration kafkaJournalConfiguration = new KafkaJournalConfiguration();
    private final NettyTransportConfiguration nettyTransportConfiguration = new NettyTransportConfiguration();
    private final PipelineConfig pipelineConfiguration = new PipelineConfig();
    private final ViewsConfig viewsConfiguration = new ViewsConfig();
    private final ProcessingStatusConfig processingStatusConfig = new ProcessingStatusConfig();
    private final JobSchedulerConfiguration jobSchedulerConfiguration = new JobSchedulerConfiguration();
    private final FreeEnterpriseConfiguration freeEnterpriseConfiguration = new FreeEnterpriseConfiguration();

    public Server() {
        super("server", configuration);
    }

    @Option(name = {"-l", "--local"}, description = "Run alertmanager in local mode. Only interesting for alertmanager developers.")
    private boolean local = false;

    public boolean isLocal() {
        return local;
    }

    @Override
    protected List<Module> getCommandBindings() {
        final ImmutableList.Builder<Module> modules = ImmutableList.builder();
        modules.add(
                new VersionAwareStorageModule(),
                new ConfigurationModule(configuration),
                new ServerBindings(configuration),
                new ElasticsearchModule(),
                new PersistenceServicesBindings(),
                new MessageFilterBindings(),
                new MessageProcessorModule(),
                new AlarmCallbackBindings(),
                new InitializerBindings(),
                new MessageInputBindings(),
                new MessageOutputBindings(configuration, chainingClassLoader),
                new RotationStrategyBindings(),
                new RetentionStrategyBindings(),
                new PeriodicalBindings(),
                new ObjectMapperModule(chainingClassLoader),
                new RestApiBindings(),
                new PasswordAlgorithmBindings(),
                new DecoratorBindings(),
                new AuditBindings(),
                new AlertConditionBindings(),
                new IndexerBindings(),
                new MigrationsModule(),
                new NetFlowPluginModule(),
                new CEFInputModule(),
                new MapWidgetModule(),
                new SidecarModule(),
                new ContentPacksModule(),
                new ViewsBindings(),
                new JobSchedulerModule(),
                new EventsModule(),
                new FreeEnterpriseModule(),
                new GRNTypesModule(),
                new SecurityModule()
        );

        return modules.build();
    }

    @Override
    protected List<Object> getCommandConfigurationBeans() {
        return Arrays.asList(configuration,
                httpConfiguration,
                elasticsearchConfiguration,
                elasticsearchClientConfiguration,
                emailConfiguration,
                mongoDbConfiguration,
                versionCheckConfiguration,
                kafkaJournalConfiguration,
                nettyTransportConfiguration,
                pipelineConfiguration,
                viewsConfiguration,
                processingStatusConfig,
                jobSchedulerConfiguration,
                freeEnterpriseConfiguration);
    }

    @Override
    protected void startNodeRegistration(Injector injector) {
        // Register this node.
        final NodeService nodeService = injector.getInstance(NodeService.class);
        final ServerStatus serverStatus = injector.getInstance(ServerStatus.class);
        final ActivityWriter activityWriter = injector.getInstance(ActivityWriter.class);
        nodeService.registerServer(serverStatus.getNodeId().toString(),
                configuration.isMaster(),
                httpConfiguration.getHttpPublishUri(),
                Tools.getLocalCanonicalHostname());
        serverStatus.setLocalMode(isLocal());
        if (configuration.isMaster() && !nodeService.isOnlyMaster(serverStatus.getNodeId())) {
            LOG.warn("Detected another master in the cluster. Retrying in {} seconds to make sure it is not "
                    + "an old stale instance.", TimeUnit.MILLISECONDS.toSeconds(configuration.getStaleMasterTimeout()));
            try {
                Thread.sleep(configuration.getStaleMasterTimeout());
            } catch (InterruptedException e) { /* nope */ }

            if (!nodeService.isOnlyMaster(serverStatus.getNodeId())) {
                // All devils here.
                String what = "Detected other master node in the cluster! Starting as non-master! "
                        + "This is a mis-configuration you should fix.";
                LOG.warn(what);
                activityWriter.write(new Activity(what, Server.class));

                // Write a notification.
                final NotificationService notificationService = injector.getInstance(NotificationService.class);
                Notification notification = notificationService.buildNow()
                        .addType(Notification.Type.MULTI_MASTER)
                        .addSeverity(Notification.Severity.URGENT);
                notificationService.publishIfFirst(notification);

                configuration.setIsMaster(false);
            } else {
                LOG.warn("Stale master has gone. Starting as master.");
            }
        }
    }

    private static class ShutdownHook implements Runnable {
        private final ActivityWriter activityWriter;
        private final ServiceManager serviceManager;
        private final NodeId nodeId;
        private final GracefulShutdown gracefulShutdown;
        private final AuditEventSender auditEventSender;

        @Inject
        public ShutdownHook(ActivityWriter activityWriter, ServiceManager serviceManager, NodeId nodeId,
                            GracefulShutdown gracefulShutdown, AuditEventSender auditEventSender) {
            this.activityWriter = activityWriter;
            this.serviceManager = serviceManager;
            this.nodeId = nodeId;
            this.gracefulShutdown = gracefulShutdown;
            this.auditEventSender = auditEventSender;
        }

        @Override
        public void run() {
            String msg = "SIGNAL received. Shutting down.";
            LOG.info(msg);
            activityWriter.write(new Activity(msg, Main.class));

            auditEventSender.success(AuditActor.system(nodeId), NODE_SHUTDOWN_INITIATE);

            gracefulShutdown.runWithoutExit();
            serviceManager.stopAsync().awaitStopped();
        }
    }

    @Override
    protected Class<? extends Runnable> shutdownHook() {
        return ShutdownHook.class;
    }

    @Override
    protected void annotateInjectorExceptions(Collection<Message> messages) {
        super.annotateInjectorExceptions(messages);
        for (Message message : messages) {
            if (message.getCause() instanceof MongoException) {
                MongoException e = (MongoException) message.getCause();
                LOG.error(UI.wallString("Unable to connect to MongoDB. Is it running and the configuration correct?\n" +
                        "Details: " + e.getMessage()));
                System.exit(-1);
            }
        }
    }

    @Override
    protected Set<ServerStatus.Capability> capabilities() {
        if (configuration.isMaster()) {
            return EnumSet.of(ServerStatus.Capability.SERVER, ServerStatus.Capability.MASTER);
        } else {
            return EnumSet.of(ServerStatus.Capability.SERVER);
        }
    }
}
