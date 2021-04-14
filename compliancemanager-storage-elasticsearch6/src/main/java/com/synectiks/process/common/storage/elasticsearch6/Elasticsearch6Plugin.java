/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.server.plugin.Plugin;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.PluginModule;
import com.synectiks.process.server.plugin.Version;

import java.util.Collection;

public class Elasticsearch6Plugin implements Plugin {
    public static final Version SUPPORTED_ES_VERSION = Version.from(6, 0, 0);

    @Override
    public PluginMetaData metadata() {
        return new Elasticsearch6Metadata();
    }

    @Override
    public Collection<PluginModule> modules() {
        return ImmutableList.of(
                new Elasticsearch6Module(),
                new ViewsESBackendModule()
        );
    }
}
