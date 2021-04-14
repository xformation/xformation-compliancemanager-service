/*
 * */
package com.synectiks.process.server.outputs;

import com.google.inject.assistedinject.Assisted;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.configuration.fields.ConfigurationField;
import com.synectiks.process.server.plugin.configuration.fields.TextField;
import com.synectiks.process.server.plugin.outputs.MessageOutput;
import com.synectiks.process.server.plugin.outputs.MessageOutputConfigurationException;
import com.synectiks.process.server.plugin.streams.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoggingOutput implements MessageOutput {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingOutput.class);
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final Configuration configuration;

    @Inject
    public LoggingOutput(@Assisted Configuration config) throws MessageOutputConfigurationException {
        LOG.info("Initializing");
        configuration = config;
        isRunning.set(true);
    }

    @Override
    public void stop() {
        isRunning.set(false);
        LOG.info("Stopping");
    }

    @Override
    public boolean isRunning() {
        return isRunning.get();
    }

    @Override
    public void write(Message message) throws Exception {
        LOG.info("{} {}", configuration.getString("prefix"), message);
    }

    @Override
    public void write(List<Message> messages) throws Exception {
        for (Message message : messages) {
            write(message);
        }
    }

    public interface Factory extends MessageOutput.Factory<LoggingOutput> {
        @Override
        LoggingOutput create(Stream stream, Configuration configuration);

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    public static class Descriptor extends MessageOutput.Descriptor {
        public Descriptor() {
            super("STDOUT Output", false, "", "An output writing every message to STDOUT.");
        }
    }


    public static class Config extends MessageOutput.Config {
        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            ConfigurationRequest configurationRequest = new ConfigurationRequest();
            configurationRequest.addField(new TextField("prefix", "Prefix", "Writing message: ", "How to prefix the message before logging it", ConfigurationField.Optional.OPTIONAL));
            return configurationRequest;
        }
    }
}
