/*
 * */
package com.synectiks.process.server.bootstrap;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.builder.CliBuilder;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.bootstrap.commands.CliCommandHelp;
import com.synectiks.process.server.bootstrap.commands.ShowVersion;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ServiceLoader;

public class Main {
    public static void main(String[] args) {
        final CliBuilder<CliCommand> builder = Cli.<CliCommand>builder("alertmanager")
                .withDescription("Open source, centralized log management")
                .withDefaultCommand(CliCommandHelp.class)
                .withCommands(ImmutableSet.of(
                        ShowVersion.class,
                        CliCommandHelp.class));

        // add rest from classpath
        final ServiceLoader<CliCommandsProvider> commandsProviders = ServiceLoader.load(CliCommandsProvider.class);
        for (CliCommandsProvider provider : commandsProviders) {
            provider.addTopLevelCommandsOrGroups(builder);
        }

        final Cli<CliCommand> cli = builder.build();
        final Runnable command = cli.parse(args);

        // Explicitly register Bouncy Castle as security provider.
        // This allows us to use more key formats than with JCE
        Security.addProvider(new BouncyCastleProvider());
        command.run();
    }
}
