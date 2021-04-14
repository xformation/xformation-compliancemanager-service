/*
 * */
package com.synectiks.process.server.plugin;

import java.util.Collection;

public interface Plugin {
    PluginMetaData metadata();
    Collection<PluginModule> modules();
}
