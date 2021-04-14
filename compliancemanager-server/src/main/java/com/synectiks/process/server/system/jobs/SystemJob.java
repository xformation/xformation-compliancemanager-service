/*
 * */
package com.synectiks.process.server.system.jobs;

import org.joda.time.DateTime;

import com.synectiks.process.server.plugin.Tools;

import static com.google.common.base.Preconditions.checkState;

public abstract class SystemJob {

    // Known types that can be resolved in the SystemJobFactory.
    public enum Type {
        FIX_DEFLECTOR_DELETE_INDEX,
        FIX_DEFLECTOR_MOVE_INDEX
    }

    public abstract void execute();

    public abstract void requestCancel();

    public abstract int getProgress();

    public abstract int maxConcurrency();

    public abstract boolean providesProgress();

    public abstract boolean isCancelable();

    public abstract String getDescription();

    public abstract String getClassName();

    public String getInfo() {
        return "No further information available.";
    }

    protected String id;
    protected DateTime startedAt;

    public String getId() {
        checkState(id != null, "Cannot return ID if the job has not been started yet.");

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void markStarted() {
        startedAt = Tools.nowUTC();
    }

    public DateTime getStartedAt() {
        return startedAt;
    }
}
