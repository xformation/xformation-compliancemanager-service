/*
 * */
package com.synectiks.process.server.rest.resources.system;

import com.codahale.metrics.annotation.Timed;
import com.github.joschi.jadconfig.util.Size;
import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.plugin.KafkaJournalConfiguration;
import com.synectiks.process.server.plugin.ThrottleState;
import com.synectiks.process.server.rest.resources.system.responses.JournalSummaryResponse;
import com.synectiks.process.server.rest.resources.system.responses.KafkaJournalConfigurationSummary;
import com.synectiks.process.server.shared.journal.Journal;
import com.synectiks.process.server.shared.journal.KafkaJournal;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kafka.log.LogSegment;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RequiresAuthentication
@Api(value = "System/Journal", description = "Message journal information of this node.")
@Produces(MediaType.APPLICATION_JSON)
@Path("/system/journal")
public class JournalResource extends RestResource {
    private static final Logger log = LoggerFactory.getLogger(JournalResource.class);
    private final boolean journalEnabled;
    private final Journal journal;
    private final KafkaJournalConfiguration kafkaJournalConfiguration;

    @Inject
    public JournalResource(Configuration configuration, KafkaJournalConfiguration kafkaJournalConfiguration, Journal journal) {
        this.kafkaJournalConfiguration = kafkaJournalConfiguration;
        this.journalEnabled = configuration.isMessageJournalEnabled();
        this.journal = journal;
    }

    @GET
    @Timed
    @ApiOperation(value = "Get current state of the journal on this node.")
    @RequiresPermissions(RestPermissions.JOURNAL_READ)
    public JournalSummaryResponse show() {
        if (!journalEnabled) {
            return JournalSummaryResponse.createDisabled();
        }

        if (journal instanceof KafkaJournal) {
            final KafkaJournal kafkaJournal = (KafkaJournal) journal;
            final ThrottleState throttleState = kafkaJournal.getThrottleState();

            long oldestSegment = Long.MAX_VALUE;
            for (final LogSegment segment : kafkaJournal.getSegments()) {
                oldestSegment = Math.min(oldestSegment, segment.created());
            }

            return JournalSummaryResponse.createEnabled(throttleState.appendEventsPerSec,
                                                        throttleState.readEventsPerSec,
                                                        throttleState.uncommittedJournalEntries,
                                                        Size.bytes(throttleState.journalSize),
                                                        Size.bytes(throttleState.journalSizeLimit),
                                                        kafkaJournal.numberOfSegments(),
                                                        new DateTime(oldestSegment, DateTimeZone.UTC),
                                                        KafkaJournalConfigurationSummary.of(kafkaJournalConfiguration)
            );

        }

        log.warn("Unknown Journal implementation {} in use, cannot get information about it. Pretending journal is disabled.",
                journal.getClass());
        return JournalSummaryResponse.createDisabled();

    }

}
