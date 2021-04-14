/*
 * */
package com.synectiks.process.server.inputs.converters;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class SyslogPriUtilities {

    public static int levelFromPriority(int priority) {
        return priority - (facilityFromPriority(priority) << 3);
    }

    public static int facilityFromPriority(int priority) {
        return priority >> 3;
    }

}
