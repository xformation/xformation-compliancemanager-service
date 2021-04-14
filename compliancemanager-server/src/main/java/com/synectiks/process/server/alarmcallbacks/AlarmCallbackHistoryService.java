/*
 * */
package com.synectiks.process.server.alarmcallbacks;

import com.google.inject.ImplementedBy;
import com.synectiks.process.server.alerts.Alert;
import com.synectiks.process.server.plugin.alarms.AlertCondition;

import java.util.List;

@ImplementedBy(AlarmCallbackHistoryServiceImpl.class)
public interface AlarmCallbackHistoryService {
    List<AlarmCallbackHistory> getForAlertId(String alertId);
    AlarmCallbackHistory save(AlarmCallbackHistory alarmCallbackHistory);
    AlarmCallbackHistory success(AlarmCallbackConfiguration alarmCallbackConfiguration, Alert alert, AlertCondition alertCondition);
    AlarmCallbackHistory error(AlarmCallbackConfiguration alarmCallbackConfiguration, Alert alert, AlertCondition alertCondition, String error);
}
