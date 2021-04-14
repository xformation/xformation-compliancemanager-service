/*
 * */
package com.synectiks.process.server.commands.journal;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.synectiks.process.server.shared.journal.KafkaJournal;

import kafka.log.LogSegment;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

@SuppressWarnings("LocalCanBeFinal")
@Command(name = "show", description = "Shows information about the persisted message journal")
public class JournalShow extends AbstractJournalCommand {

    @Option(name = {"-s", "--show-segments"}, description = "Show detail information for all segments")
    private boolean showSegmentDetails = false;

    public JournalShow() {
        super("show-journal");
    }

    @Override
    protected void runCommand() {
        long sizeInBytes = journal.size();
        int numSegments = journal.numberOfSegments();
        long committedReadOffset = journal.getCommittedReadOffset();
        final StringBuilder sb = new StringBuilder();

        final long startOffset = journal.getLogStartOffset();
        final long lastOffset = journal.getLogEndOffset() - 1;

        sb.append("alertmanager message journal in directory: ").append(kafkaJournalConfiguration.getMessageJournalDir().toAbsolutePath()).append(
                "\n");
        sb.append("\t").append("Total size in bytes: ").append(sizeInBytes).append("\n");
        sb.append("\t").append("Number of segments: ").append(numSegments).append("\n");
        sb.append("\t").append("Log start offset: ").append(startOffset).append("\n");
        sb.append("\t").append("Log end offset: ").append(lastOffset).append("\n");
        sb.append("\t").append("Number of messages: ").append(lastOffset - startOffset + 1).append("\n");
        sb.append("\t").append("Committed read offset: ");
        if (committedReadOffset == Long.MIN_VALUE) {
            sb.append("nothing committed");
        } else {
            sb.append(committedReadOffset);
        }
        sb.append("\n");

        if (showSegmentDetails) {
            appendSegmentDetails(journal, sb);
        }
        sb.append("\n");

        System.out.print(sb);
        System.out.flush();
    }

    private void appendSegmentDetails(KafkaJournal journal, StringBuilder sb) {
        final Iterable<LogSegment> segments = journal.getSegments();
        int i = 1;
        for (LogSegment segment : segments) {
            sb.append("\t\t").append("Segment ").append(i++).append("\n");
            sb.append("\t\t\t").append("Base offset: ").append(segment.baseOffset()).append("\n");
            sb.append("\t\t\t").append("Size in bytes: ").append(segment.size()).append("\n");
            sb.append("\t\t\t").append("Created at: ").append(new DateTime(segment.created(), DateTimeZone.UTC)).append("\n");
            sb.append("\t\t\t").append("Last modified: ").append(new DateTime(segment.lastModified(), DateTimeZone.UTC)).append("\n");
        }
    }

}
