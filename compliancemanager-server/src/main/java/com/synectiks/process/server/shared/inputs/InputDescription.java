/*
 * */
package com.synectiks.process.server.shared.inputs;

import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.inputs.MessageInput;

import java.util.Map;

public class InputDescription {
    private final MessageInput.Descriptor descriptor;
    private final MessageInput.Config config;

    public InputDescription(MessageInput.Descriptor descriptor, MessageInput.Config config) {
        this.descriptor = descriptor;
        this.config = config;
    }

    public String getName() {
        return descriptor.getName();
    }

    public boolean isExclusive() {
        return descriptor.isExclusive();
    }

    public String getLinkToDocs() {
        return descriptor.getLinkToDocs();
    }

    public Map<String, Map<String, Object>> getRequestedConfiguration() {
        return config.combinedRequestedConfiguration().asList();
    }

    public ConfigurationRequest getConfigurationRequest() {
        return config.combinedRequestedConfiguration();
    }
}
