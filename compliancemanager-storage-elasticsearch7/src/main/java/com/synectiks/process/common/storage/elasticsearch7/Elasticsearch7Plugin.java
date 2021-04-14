/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch7;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.plugin.Plugin;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.PluginModule;
import com.synectiks.process.server.plugin.Version;

import java.util.Collection;

public class Elasticsearch7Plugin implements Plugin {
    public static final Version SUPPORTED_ES_VERSION = Version.from(7, 0, 0);

    @Override
    public PluginMetaData metadata() {
        return new Elasticsearch7Metadata();
    }

    @Override
    public Collection<PluginModule> modules() {
        return ImmutableSet.of(
                new Elasticsearch7Module(),
                new ViewsESBackendModule()
        );
    }
}
