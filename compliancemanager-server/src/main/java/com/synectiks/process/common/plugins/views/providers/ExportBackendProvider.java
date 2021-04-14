/*
 * */
package com.synectiks.process.common.plugins.views.providers;

import com.synectiks.process.common.plugins.views.search.export.ExportBackend;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.VersionAwareProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class ExportBackendProvider extends VersionAwareProvider<ExportBackend> {
    @Inject
    public ExportBackendProvider(@ElasticsearchVersion Version version, Map<Version, Provider<ExportBackend>> pluginBindings) {
        super(version, pluginBindings);
    }
}
