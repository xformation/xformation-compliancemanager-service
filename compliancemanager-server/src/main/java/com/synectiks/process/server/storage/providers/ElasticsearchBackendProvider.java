/*
 * */
package com.synectiks.process.server.storage.providers;

import com.synectiks.process.common.plugins.views.search.engine.GeneratedQueryContext;
import com.synectiks.process.common.plugins.views.search.engine.QueryBackend;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.VersionAwareProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class ElasticsearchBackendProvider extends VersionAwareProvider<QueryBackend<? extends GeneratedQueryContext>> {
    @Inject
    public ElasticsearchBackendProvider(@ElasticsearchVersion Version version,
                                        Map<Version, Provider<QueryBackend<? extends GeneratedQueryContext>>> pluginBindings) {
        super(version, pluginBindings);
    }
}
