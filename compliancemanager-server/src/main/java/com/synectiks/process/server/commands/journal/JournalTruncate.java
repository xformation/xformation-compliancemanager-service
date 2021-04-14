/*
 * */
package com.synectiks.process.server.commands.journal;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;

@Command(name = "truncate", description = "Truncates the journal to a given offset")
public class JournalTruncate extends AbstractJournalCommand {

    @Option(name = {"-o", "--offset"}, description = "Truncate journal up to this offset, no remaining offset will be larger than the given offset.")
    @Required
    private long offset = Long.MIN_VALUE;

    public JournalTruncate() {
        super("truncate-journal");
    }

    @Override
    protected void runCommand() {
        try {
            final long logEndOffset = journal.getLogEndOffset();
            if (offset > logEndOffset) {
                System.err.println("Truncating journal to " + offset + " has no effect as the largest offset in the log is " + (logEndOffset - 1) + ".");
            } else {
                journal.truncateTo(offset);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal offset value " + offset);
        }
    }
}
