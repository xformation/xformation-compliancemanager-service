/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.syslog;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SyslogPriorityAsString {
    public abstract String getLevel();

    public abstract String getFacility();

    public static SyslogPriorityAsString create(String level, String facility) {
        return new AutoValue_SyslogPriorityAsString(level, facility);
    }
}
