/*
 * */
package com.synectiks.process.common.scheduler.periodicals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.common.scheduler.DBJobTriggerService;
import com.synectiks.process.server.plugin.periodical.Periodical;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class ScheduleTriggerCleanUp extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(ScheduleTriggerCleanUp.class);

    private final DBJobTriggerService dbJobTriggerService;

    // Remove completed job triggers after a day
    private static final long OUTOFDATE_IN_DAYS = 1;

    @Inject
    public ScheduleTriggerCleanUp(DBJobTriggerService dbJobTriggerService) {
        this.dbJobTriggerService = dbJobTriggerService;
    }

    @Override
    public boolean runsForever() {
        return false;
    }

    @Override
    public boolean stopOnGracefulShutdown() {
        return true;
    }

    @Override
    public boolean masterOnly() {
        return true;
    }

    @Override
    public boolean startOnThisNode() {
        return true;
    }

    @Override
    public boolean isDaemon() {
        return true;
    }

    @Override
    public int getInitialDelaySeconds() {
        return 120;
    }

    @Override
    public int getPeriodSeconds() {
        return 86400;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public void doRun() {
        int deleted = dbJobTriggerService.deleteCompletedOnceSchedulesOlderThan(1, TimeUnit.DAYS);
        if (deleted > 0) {
            LOG.debug("Deleted {} outdated OnceJobSchedule triggers.", deleted);
        }
    }
}
