/*
 * */
package com.synectiks.process.server.periodical;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge;
import com.github.joschi.jadconfig.util.Size;
import com.google.common.eventbus.EventBus;
import com.synectiks.process.server.notifications.Notification;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.plugin.GlobalMetricNames;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.ThrottleState;
import com.synectiks.process.server.plugin.periodical.Periodical;
import com.synectiks.process.server.shared.buffers.ProcessBuffer;
import com.synectiks.process.server.shared.journal.Journal;
import com.synectiks.process.server.shared.journal.KafkaJournal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.synectiks.process.server.shared.metrics.MetricUtils.safelyRegister;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * The ThrottleStateUpdater publishes the current state buffer state of the journal to other interested parties,
 * chiefly the ThrottleableTransports.
 * <p/>
 * <p>
 * It only includes the necessary information to make a decision about whether to throttle parts of the system,
 * but does not send "throttle" commands. This allows for a flexible approach in picking a throttling strategy.
 * </p>
 * <p>
 * The implementation expects to be called once per second to have a rough estimate about the events per second,
 * over the last second.
 * </p>
 */
public class ThrottleStateUpdaterThread extends Periodical {
    private static final Logger log = LoggerFactory.getLogger(ThrottleStateUpdaterThread.class);
    private final KafkaJournal journal;
    private final ProcessBuffer processBuffer;
    private final EventBus eventBus;
    private final Size retentionSize;
    private final NotificationService notificationService;
    private final ServerStatus serverStatus;

    private boolean firstRun = true;
    private long logEndOffset;
    private long currentReadOffset;
    private long currentTs;
    private ThrottleState throttleState;

    @Inject
    public ThrottleStateUpdaterThread(final Journal journal,
                                      ProcessBuffer processBuffer,
                                      EventBus eventBus,
                                      NotificationService notificationService,
                                      ServerStatus serverStatus,
                                      MetricRegistry metricRegistry,
                                      @Named("message_journal_max_size") Size retentionSize) {
        this.processBuffer = processBuffer;
        this.eventBus = eventBus;
        this.retentionSize = retentionSize;
        this.notificationService = notificationService;
        this.serverStatus = serverStatus;
        // leave this.journal null, we'll say "don't start" in that case, see startOnThisNode() below.
        if (journal instanceof KafkaJournal) {
            this.journal = (KafkaJournal) journal;
        } else {
            this.journal = null;
        }
        throttleState = new ThrottleState();

        safelyRegister(metricRegistry,
                       GlobalMetricNames.JOURNAL_APPEND_RATE,
                       new Gauge<Long>() {
                           @Override
                           public Long getValue() {
                               return throttleState.appendEventsPerSec;
                           }
                       });
        safelyRegister(metricRegistry,
                       GlobalMetricNames.JOURNAL_READ_RATE,
                       new Gauge<Long>() {
                           @Override
                           public Long getValue() {
                               return throttleState.readEventsPerSec;
                           }
                       });
        safelyRegister(metricRegistry,
                       GlobalMetricNames.JOURNAL_SEGMENTS,
                       new Gauge<Integer>() {
                           @Override
                           public Integer getValue() {
                               if (ThrottleStateUpdaterThread.this.journal == null) {
                                   return 0;
                               }
                               return ThrottleStateUpdaterThread.this.journal.numberOfSegments();
                           }
                       });
        safelyRegister(metricRegistry,
                       GlobalMetricNames.JOURNAL_UNCOMMITTED_ENTRIES,
                       new Gauge<Long>() {
                           @Override
                           public Long getValue() {
                               return throttleState.uncommittedJournalEntries;
                           }
                       });
        final Gauge<Long> sizeGauge = safelyRegister(metricRegistry,
                                   GlobalMetricNames.JOURNAL_SIZE,
                                   new Gauge<Long>() {
                                       @Override
                                       public Long getValue() {
                                           return throttleState.journalSize;
                                       }
                                   });
        final Gauge<Long> sizeLimitGauge = safelyRegister(metricRegistry,
                                        GlobalMetricNames.JOURNAL_SIZE_LIMIT,
                                        new Gauge<Long>() {
                                            @Override
                                            public Long getValue() {
                                                return throttleState.journalSizeLimit;
                                            }
                                        });
        safelyRegister(metricRegistry,
                       GlobalMetricNames.JOURNAL_UTILIZATION_RATIO,
                       new RatioGauge() {
                           @Override
                           protected Ratio getRatio() {
                               return Ratio.of(sizeGauge.getValue(),
                                               sizeLimitGauge.getValue());
                           }
                       });
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
        return false;
    }

    @Override
    public boolean startOnThisNode() {
        // don't start if we don't have the KafkaJournal
        return journal != null;
    }

    @Override
    public boolean isDaemon() {
        return true;
    }

    @Override
    public int getInitialDelaySeconds() {
        return 1;
    }

    @Override
    public int getPeriodSeconds() {
        return 1;
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public void doRun() {
        throttleState = new ThrottleState(throttleState);
        final long committedOffset = journal.getCommittedOffset();

        // TODO there's a lot of duplication around this class. Probably should be refactored a bit.
        // also update metrics for each of the values, so clients can get to it cheaply

        long prevTs = currentTs;
        currentTs = System.nanoTime();

        long previousLogEndOffset = logEndOffset;
        long previousReadOffset = currentReadOffset;
        long logStartOffset = journal.getLogStartOffset();
        logEndOffset = journal.getLogEndOffset() - 1; // -1 because getLogEndOffset is the next offset that gets assigned
        currentReadOffset = journal.getNextReadOffset() - 1; // just to make it clear which field we read

        // for the first run, don't send an update, there's no previous data available to calc rates
        if (firstRun) {
            firstRun = false;
            return;
        }

        throttleState.appendEventsPerSec = (long) Math.floor((logEndOffset - previousLogEndOffset) / ((currentTs - prevTs) / 1.0E09));
        throttleState.readEventsPerSec = (long) Math.floor((currentReadOffset - previousReadOffset) / ((currentTs - prevTs) / 1.0E09));

        throttleState.journalSize = journal.size();
        throttleState.journalSizeLimit = retentionSize.toBytes();

        throttleState.processBufferCapacity = processBuffer.getRemainingCapacity();

        if (committedOffset == KafkaJournal.DEFAULT_COMMITTED_OFFSET) {
            // nothing committed at all, the entire log is uncommitted, or completely empty.
            throttleState.uncommittedJournalEntries = journal.size() == 0 ? 0 : logEndOffset - logStartOffset;
        } else {
            throttleState.uncommittedJournalEntries = logEndOffset - committedOffset;
        }
        log.debug("ThrottleState: {}", throttleState);

        // the journal needs this to provide information to rest clients
        journal.setThrottleState(throttleState);
        
        // publish to interested parties
        eventBus.post(throttleState);

        // Abusing the current thread to send notifications from KafkaJournal in the alertmanager2-shared module
        final double journalUtilizationPercentage = throttleState.journalSizeLimit > 0 ? (throttleState.journalSize * 100) / throttleState.journalSizeLimit : 0.0;

        if (journalUtilizationPercentage > KafkaJournal.NOTIFY_ON_UTILIZATION_PERCENTAGE) {
            Notification notification = notificationService.buildNow()
                    .addNode(serverStatus.getNodeId().toString())
                    .addType(Notification.Type.JOURNAL_UTILIZATION_TOO_HIGH)
                    .addSeverity(Notification.Severity.URGENT)
                    .addDetail("journal_utilization_percentage", journalUtilizationPercentage);
            notificationService.publishIfFirst(notification);
        }

        if (journal.getPurgedSegmentsInLastRetention() > 0) {
            Notification notification = notificationService.buildNow()
                    .addNode(serverStatus.getNodeId().toString())
                    .addType(Notification.Type.JOURNAL_UNCOMMITTED_MESSAGES_DELETED)
                    .addSeverity(Notification.Severity.URGENT);
            notificationService.publishIfFirst(notification);
        }
    }
}
