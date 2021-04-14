/*
 * */
package com.synectiks.process.server.storage.providers;

import com.synectiks.process.common.events.search.MoreSearchAdapter;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.VersionAwareProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class MoreSearchAdapterProvider extends VersionAwareProvider<MoreSearchAdapter> {
    @Inject
    public MoreSearchAdapterProvider(@ElasticsearchVersion Version version, Map<Version, Provider<MoreSearchAdapter>> pluginBindings) {
        super(version, pluginBindings);
    }
}
