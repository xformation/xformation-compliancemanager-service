/*
 * */
package com.synectiks.process.server.periodical;

import com.google.common.collect.ImmutableSortedSet;
import com.synectiks.process.server.migrations.Migration;
import com.synectiks.process.server.plugin.periodical.Periodical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Set;
import java.util.SortedSet;

public class ConfigurationManagementPeriodical extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationManagementPeriodical.class);
    private final SortedSet<Migration> migrations;

    @Inject
    public ConfigurationManagementPeriodical(Set<Migration> migrations) {
        this.migrations = ImmutableSortedSet.copyOf(migrations);
    }

    @Override
    public void doRun() {
        for(Migration migration : migrations) {
            try {
                migration.upgrade();
            } catch (Exception e) {
                LOG.error("Error while running migration <{}>", migration, e);
            }
        }
    }

    @Override
    public boolean runsForever() {
        return true;
    }

    @Override
    public boolean stopOnGracefulShutdown() {
        return false;
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
        return 0;
    }

    @Override
    public int getPeriodSeconds() {
        return 0;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }
}
