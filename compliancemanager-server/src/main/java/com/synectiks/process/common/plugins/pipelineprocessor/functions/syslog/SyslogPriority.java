/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.syslog;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SyslogPriority {
    public abstract int getLevel();

    public abstract int getFacility();

    public static SyslogPriority create(int level, int facility) {
        return new AutoValue_SyslogPriority(level, facility);
    }
}
