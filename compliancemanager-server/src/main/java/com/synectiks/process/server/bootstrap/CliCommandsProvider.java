/*
 * */
package com.synectiks.process.server.bootstrap;


import com.github.rvesse.airline.builder.CliBuilder;

/**
 * This class provides the opportunity to add top level commands or command groups to the bootstrap processes.
 */
public interface CliCommandsProvider {
    void addTopLevelCommandsOrGroups(CliBuilder<CliCommand> builder);
}
