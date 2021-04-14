/*
 * */
package com.synectiks.process.server.alerts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfiguration;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfigurationService;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackFactory;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackHistory;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackHistoryService;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.alarms.callbacks.AlarmCallback;
import com.synectiks.process.server.plugin.streams.Stream;

import javax.inject.Inject;
import java.util.List;

public class AlertNotificationsSender {
    private static final Logger LOG = LoggerFactory.getLogger(AlertNotificationsSender.class);

    private final AlarmCallbackConfigurationService alarmCallbackConfigurationService;
    private final AlarmCallbackFactory alarmCallbackFactory;
    private final AlarmCallbackHistoryService alarmCallbackHistoryService;

    @Inject
    public AlertNotificationsSender(AlarmCallbackConfigurationService alarmCallbackConfigurationService,
                                    AlarmCallbackFactory alarmCallbackFactory,
                                    AlarmCallbackHistoryService alarmCallbackHistoryService) {
        this.alarmCallbackConfigurationService = alarmCallbackConfigurationService;
        this.alarmCallbackFactory = alarmCallbackFactory;
        this.alarmCallbackHistoryService = alarmCallbackHistoryService;
    }

    public void send(AlertCondition.CheckResult result, Stream stream, Alert alert, AlertCondition alertCondition) {
        final List<AlarmCallbackConfiguration> callConfigurations = alarmCallbackConfigurationService.getForStream(stream);

        // Checking if alarm callbacks have been defined
        for (AlarmCallbackConfiguration configuration : callConfigurations) {
            AlarmCallbackHistory alarmCallbackHistory;
            AlarmCallback alarmCallback = null;
            try {
                alarmCallback = alarmCallbackFactory.create(configuration);
                alarmCallback.call(stream, result);
                alarmCallbackHistory = alarmCallbackHistoryService.success(configuration, alert, alertCondition);
            } catch (Exception e) {
                if (alarmCallback != null) {
                    LOG.warn("Alarm callback <" + alarmCallback.getName() + "> failed. Skipping.", e);
                } else {
                    LOG.warn("Alarm callback with id " + configuration.getId() + " failed. Skipping.", e);
                }
                alarmCallbackHistory = alarmCallbackHistoryService.error(configuration, alert, alertCondition, e.getMessage());
            }

            try {
                alarmCallbackHistoryService.save(alarmCallbackHistory);
            } catch (Exception e) {
                LOG.warn("Unable to save history of alarm callback run: ", e);
            }
        }
    }
}
