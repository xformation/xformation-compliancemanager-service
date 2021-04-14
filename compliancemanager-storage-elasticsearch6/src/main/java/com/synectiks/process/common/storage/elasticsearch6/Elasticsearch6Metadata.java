/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.Version;

public class Elasticsearch6Metadata implements PluginMetaData {
    @Override
    public String getUniqueId() {
        return Elasticsearch6Plugin.class.getCanonicalName();
    }

    @Override
    public String getName() {
        return "Elasticsearch 6 Support";
    }

    @Override
    public String getAuthor() {
        return "Graylog, Inc.";
    }

    @Override
    public URI getURL() {
        return URI.create("https://www.graylog.org");
    }

    @Override
    public Version getVersion() {
        return Version.CURRENT_CLASSPATH;
    }

    @Override
    public String getDescription() {
        return "Support for Elasticsearch 6";
    }

    @Override
    public Version getRequiredVersion() {
        return Version.CURRENT_CLASSPATH;
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}
