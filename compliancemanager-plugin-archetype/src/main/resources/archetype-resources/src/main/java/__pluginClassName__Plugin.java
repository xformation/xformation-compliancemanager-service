package ${package};

import com.synectiks.process.server.plugin.Plugin;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.PluginModule;

import java.util.Collection;
import java.util.Collections;

/**
 * Implement the Plugin interface here.
 */
public class ${pluginClassName}Plugin implements Plugin {
    @Override
    public PluginMetaData metadata() {
        return new ${pluginClassName}MetaData();
    }

    @Override
    public Collection<PluginModule> modules () {
        return Collections.<PluginModule>singletonList(new ${pluginClassName}Module());
    }
}
