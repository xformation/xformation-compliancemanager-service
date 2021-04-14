/*
 * */
package com.synectiks.process.server.commands;

import com.github.rvesse.airline.builder.CliBuilder;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.bootstrap.CliCommand;
import com.synectiks.process.server.bootstrap.CliCommandsProvider;
import com.synectiks.process.server.commands.journal.JournalDecode;
import com.synectiks.process.server.commands.journal.JournalShow;
import com.synectiks.process.server.commands.journal.JournalTruncate;

public class ServerCommandsProvider implements CliCommandsProvider {
    @Override
    public void addTopLevelCommandsOrGroups(CliBuilder<CliCommand> builder) {

        builder.withCommand(Server.class);

        builder.withGroup("journal")
                .withDescription("Manage the persisted message journal")
                .withDefaultCommand(JournalShow.class)
                .withCommands(
                        ImmutableSet.of(
                                JournalShow.class,
                                JournalTruncate.class,
                                JournalDecode.class
                        ));

    }
}
