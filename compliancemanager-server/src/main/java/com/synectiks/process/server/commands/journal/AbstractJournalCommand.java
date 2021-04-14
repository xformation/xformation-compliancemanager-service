/*
 * */
package com.synectiks.process.server.commands.journal;

import com.google.inject.Module;
import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.audit.AuditBindings;
import com.synectiks.process.server.bindings.ConfigurationModule;
import com.synectiks.process.server.bootstrap.CmdLineTool;
import com.synectiks.process.server.plugin.KafkaJournalConfiguration;
import com.synectiks.process.server.plugin.Plugin;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.shared.bindings.SchedulerBindings;
import com.synectiks.process.server.shared.bindings.ServerStatusBindings;
import com.synectiks.process.server.shared.journal.KafkaJournal;
import com.synectiks.process.server.shared.journal.KafkaJournalModule;
import com.synectiks.process.server.shared.plugins.ChainingClassLoader;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class AbstractJournalCommand extends CmdLineTool {
    protected static final Configuration configuration = new Configuration();
    protected final KafkaJournalConfiguration kafkaJournalConfiguration = new KafkaJournalConfiguration();
    protected KafkaJournal journal;

    public AbstractJournalCommand() {
        this(null);
    }
    public AbstractJournalCommand(String commandName) {
        super(commandName, configuration);
    }

    @Override
    protected List<Module> getCommandBindings() {
        return Arrays.asList(new ConfigurationModule(configuration),
                             new ServerStatusBindings(capabilities()),
                             new SchedulerBindings(),
                             new KafkaJournalModule(),
                             new AuditBindings());
    }

    @Override
    protected Set<ServerStatus.Capability> capabilities() {
        return configuration.isMaster() ? Collections.singleton(ServerStatus.Capability.MASTER) : Collections.emptySet();
    }

    @Override
    protected List<Object> getCommandConfigurationBeans() {
        return Arrays.asList(configuration, kafkaJournalConfiguration);
    }

    @Override
    protected boolean onlyLogErrors() {
        // we don't want any non-error log output
        return true;
    }

    @Override
    protected Set<Plugin> loadPlugins(Path pluginPath, ChainingClassLoader chainingClassLoader) {
        // these commands do not need plugins, which could cause problems because of not loaded config beans
        return Collections.emptySet();
    }

    @Override
    protected void startCommand() {
        try {
            journal = injector.getInstance(KafkaJournal.class);
            runCommand();
        } catch (Exception e) {
            System.err.println(
                    "Unable to read the message journal. Please make sure no other alertmanager process is using the journal.");
        } finally {
            if (journal != null) journal.stopAsync().awaitTerminated();
        }
    }

    protected abstract void runCommand();
}
