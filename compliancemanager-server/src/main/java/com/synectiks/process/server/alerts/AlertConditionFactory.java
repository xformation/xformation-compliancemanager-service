/*
 * */
package com.synectiks.process.server.alerts;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationException;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.streams.Stream;

import javax.inject.Inject;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

public class AlertConditionFactory {
    private static final Logger LOG = LoggerFactory.getLogger(AlertConditionFactory.class);
    private final Map<String, AlertCondition.Factory> alertConditionMap;

    @Inject
    public AlertConditionFactory(Map<String, AlertCondition.Factory> alertConditionMap) {
        this.alertConditionMap = alertConditionMap;
    }

    public AlertCondition createAlertCondition(String type,
                                               Stream stream,
                                               String id,
                                               DateTime createdAt,
                                               String creatorId,
                                               Map<String, Object> parameters,
                                               String title) throws ConfigurationException {

        final String conditionTitle = isNullOrEmpty(title) ? "" : "'" + title + "' ";
        final AlertCondition.Factory factory = this.alertConditionMap.get(type);
        checkArgument(factory != null, "Unknown alert condition type <%s> for alert condition %s<%s> on stream \"%s\" <%s>",
                type, conditionTitle, id, stream.getTitle(), stream.getId());

        /*
         * Ensure the given parameters fulfill the requested configuration preconditions.
         * Here we strictly use the Configuration object to verify the configuration and don't pass it down to
         * the factory. The reason for this is that Configuration only support int values, but at least an
         * alert condition expects a double.
         */
        try {
            final ConfigurationRequest requestedConfiguration = factory.config().getRequestedConfiguration();
            final Configuration configuration = new Configuration(parameters);
            requestedConfiguration.check(configuration);
        } catch (ConfigurationException e) {
            LOG.error("Could not load alert condition {}<{}> on stream \"{}\" <{}>, invalid configuration detected.", conditionTitle, id, stream.getTitle(), stream.getId());
            throw e;
        }

        return factory.create(stream, id, createdAt, creatorId, parameters, title);
    }
}
