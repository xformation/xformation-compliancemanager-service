/*
 * */
package com.synectiks.process.server.plugin.alarms.callbacks;

import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationException;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.streams.Stream;

import java.util.Map;

public interface AlarmCallback {
    void initialize(Configuration config) throws AlarmCallbackConfigurationException;

    void call(Stream stream, AlertCondition.CheckResult result) throws AlarmCallbackException;

    ConfigurationRequest getRequestedConfiguration();

    String getName();

    Map<String, Object> getAttributes();

    void checkConfiguration() throws ConfigurationException;
}
