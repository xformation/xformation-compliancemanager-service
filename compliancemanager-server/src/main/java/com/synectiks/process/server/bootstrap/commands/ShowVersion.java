/*
 * */
package com.synectiks.process.server.bootstrap.commands;

import com.github.rvesse.airline.annotations.Command;
import com.synectiks.process.server.bootstrap.CliCommand;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.Version;

@Command(name = "version", description = "Show the alertmanager and JVM versions")
public class ShowVersion implements CliCommand {
    private final Version version = Version.CURRENT_CLASSPATH;

    @Override
    public void run() {
        System.out.println("alertmanager " + version);
        System.out.println("JRE: " + Tools.getSystemInformation());
    }
}
