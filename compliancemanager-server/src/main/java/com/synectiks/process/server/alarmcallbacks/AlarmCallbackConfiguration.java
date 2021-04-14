/*
 * */
package com.synectiks.process.server.alarmcallbacks;

import java.util.Date;
import java.util.Map;

public interface AlarmCallbackConfiguration {
    String getId();
    String getStreamId();
    String getType();
    String getTitle();
    Map<String, Object> getConfiguration();
    Date getCreatedAt();
    String getCreatorUserId();
}
