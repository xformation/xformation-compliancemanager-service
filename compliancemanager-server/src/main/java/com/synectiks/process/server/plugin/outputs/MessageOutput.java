/*
 * */
package com.synectiks.process.server.plugin.outputs;

import com.synectiks.process.server.plugin.AbstractDescriptor;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.Stoppable;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.streams.Output;
import com.synectiks.process.server.plugin.streams.Stream;

import java.util.List;

public interface MessageOutput extends Stoppable {
    // This factory is implemented by output plugins that have been built before alertmanager 3.0.1.
    // We have to keep it around to make sure older plugins still load with alertmanager >=3.0.1.
    // It can be removed once we decide to stop supporting old plugins.
    interface Factory<T> {
        T create(Stream stream, Configuration configuration);
        Config getConfig();
        Descriptor getDescriptor();
    }

    // This is the factory that should be implemented by output plugins which target alertmanager 3.0.1 and later.
    // The only change compared to Factory is that it also takes the Output instance parameter.
    interface Factory2<T> {
        T create(Output output, Stream stream, Configuration configuration);
        Config getConfig();
        Descriptor getDescriptor();
    }

    class Descriptor extends AbstractDescriptor {
        private final String humanName;

        protected Descriptor() {
            throw new IllegalStateException("This class should not be instantiated directly, this is a bug.");
        }

        public Descriptor(String name, boolean exclusive, String linkToDocs, String humanName) {
            super(name, exclusive, linkToDocs);
            this.humanName = humanName;
        }

        public String getHumanName() {
            return humanName;
        }
    }

    class Config {
        public ConfigurationRequest getRequestedConfiguration() {
            return new ConfigurationRequest();
        }
    }

    boolean isRunning();
    void write(Message message) throws Exception;
    void write(List<Message> messages) throws Exception;
}
