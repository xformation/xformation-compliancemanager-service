/*
 * */
package com.synectiks.process.server.storage.providers;

import com.synectiks.process.server.indexer.messages.MessagesAdapter;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.VersionAwareProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

public class MessagesAdapterProvider extends VersionAwareProvider<MessagesAdapter> {
    @Inject
    public MessagesAdapterProvider(@ElasticsearchVersion Version version, Map<Version, Provider<MessagesAdapter>> pluginBindings) {
        super(version, pluginBindings);
    }
}
