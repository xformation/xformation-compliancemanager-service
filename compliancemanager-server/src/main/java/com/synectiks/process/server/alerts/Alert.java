/*
 * */
package com.synectiks.process.server.alerts;

import org.joda.time.DateTime;

import java.util.Map;

public interface Alert {
    String getId();
    String getStreamId();
    String getConditionId();
    DateTime getTriggeredAt();
    DateTime getResolvedAt();
    String getDescription();
    Map<String, Object> getConditionParameters();
    boolean isInterval();

    enum AlertState {
        ANY, RESOLVED, UNRESOLVED;

        public static AlertState fromString(String state) {
            for (AlertState aState : AlertState.values()) {
                if (aState.toString().equalsIgnoreCase(state)) {
                    return aState;
                }
            }

            throw new IllegalArgumentException("Alert state " + state + " is not supported");
        }
    }
}
