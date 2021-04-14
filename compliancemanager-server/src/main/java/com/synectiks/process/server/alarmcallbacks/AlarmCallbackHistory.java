/*
 * */
package com.synectiks.process.server.alarmcallbacks;

import org.joda.time.DateTime;

import com.synectiks.process.server.rest.models.alarmcallbacks.AlarmCallbackResult;
import com.synectiks.process.server.rest.models.alarmcallbacks.AlarmCallbackSummary;

public interface AlarmCallbackHistory {
    String id();
    AlarmCallbackSummary alarmcallbackConfiguration();
    String alertId();
    String alertConditionId();
    AlarmCallbackResult result();
    DateTime createdAt();
}
