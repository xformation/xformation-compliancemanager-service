/*
 * */
package com.synectiks.process.server.periodical;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.migrations.Migration;
import com.synectiks.process.server.periodical.ConfigurationManagementPeriodical;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;

public class ConfigurationManagementPeriodicalTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Spy
    private Migration migration1 = stubMigration(ZonedDateTime.of(2016, 12, 1, 0, 0, 0, 0, ZoneOffset.UTC));
    @Spy
    private Migration migration2 = stubMigration(ZonedDateTime.of(2016, 11, 1, 0, 0, 0, 0, ZoneOffset.UTC));
    @Spy
    private Migration migration3 = stubMigration(ZonedDateTime.of(2016, 10, 1, 0, 0, 0, 0, ZoneOffset.UTC));

    private ConfigurationManagementPeriodical periodical;

    @Before
    public void setUp() throws Exception {
        periodical = new ConfigurationManagementPeriodical(ImmutableSet.of(migration1, migration2, migration3));
    }

    @Test
    public void doRunExecutesMigrationOrderedByCreatedAtAscending() throws Exception {
        final InOrder invocationOrder = inOrder(migration1, migration2, migration3);

        periodical.doRun();

        invocationOrder.verify(migration3).upgrade();
        invocationOrder.verify(migration2).upgrade();
        invocationOrder.verify(migration1).upgrade();
    }

    @Test
    public void doRunExecutesMigrationIfExceptionIsThrown() throws Exception {
        final InOrder invocationOrder = inOrder(migration1, migration2, migration3);

        doThrow(new RuntimeException("Boom!")).when(migration3).upgrade();

        periodical.doRun();

        invocationOrder.verify(migration3).upgrade();
        invocationOrder.verify(migration2).upgrade();
        invocationOrder.verify(migration1).upgrade();
    }

    @Test
    public void runsForever() throws Exception {
        assertThat(periodical.runsForever()).isTrue();
    }

    @Test
    public void stopOnGracefulShutdown() throws Exception {
        assertThat(periodical.stopOnGracefulShutdown()).isFalse();
    }

    @Test
    public void masterOnly() throws Exception {
        assertThat(periodical.masterOnly()).isTrue();
    }

    @Test
    public void startOnThisNode() throws Exception {
        assertThat(periodical.startOnThisNode()).isTrue();
    }

    @Test
    public void isDaemon() throws Exception {
        assertThat(periodical.isDaemon()).isTrue();
    }

    @Test
    public void getInitialDelaySeconds() throws Exception {
        assertThat(periodical.getInitialDelaySeconds()).isEqualTo(0);
    }

    @Test
    public void getPeriodSeconds() throws Exception {
        assertThat(periodical.getPeriodSeconds()).isEqualTo(0);
    }

    @Test
    public void getLogger() throws Exception {
        assertThat(periodical.getLogger()).isNotNull();
    }

    private Migration stubMigration(final ZonedDateTime zonedDateTime) {
        return new Migration() {
            @Override
            public ZonedDateTime createdAt() {
                return zonedDateTime;
            }

            @Override
            public void upgrade() {
            }
        };
    }
}