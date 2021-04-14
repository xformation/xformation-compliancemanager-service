/*
 * */
package com.synectiks.process.server.plugin;

import java.net.URI;
import java.util.Set;

public interface PluginMetaData {
    String getUniqueId();

    String getName();

    String getAuthor();

    URI getURL();

    Version getVersion();

    String getDescription();

    Version getRequiredVersion();

    Set<ServerStatus.Capability> getRequiredCapabilities();
}
