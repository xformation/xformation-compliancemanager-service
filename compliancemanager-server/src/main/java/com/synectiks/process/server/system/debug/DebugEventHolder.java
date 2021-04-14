/*
 * */
package com.synectiks.process.server.system.debug;

public class DebugEventHolder {
    private static volatile DebugEvent clusterEvent;
    private static volatile DebugEvent localEvent;

    public static DebugEvent getClusterDebugEvent() {
        return clusterEvent;
    }

    public static DebugEvent getLocalDebugEvent() {
        return localEvent;
    }

    public static void setClusterDebugEvent(DebugEvent event) {
        DebugEventHolder.clusterEvent = event;
    }

    public static void setLocalDebugEvent(DebugEvent event) {
        DebugEventHolder.localEvent = event;
    }
}
