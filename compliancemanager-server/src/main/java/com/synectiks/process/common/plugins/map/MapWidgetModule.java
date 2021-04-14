/*
 * */
package com.synectiks.process.common.plugins.map;

import com.synectiks.process.common.plugins.map.geoip.MaxmindDataAdapter;
import com.synectiks.process.common.plugins.map.geoip.processor.GeoIpProcessor;
import com.synectiks.process.server.plugin.PluginModule;

public class MapWidgetModule extends PluginModule {
    @Override
    protected void configure() {
        addMessageProcessor(GeoIpProcessor.class, GeoIpProcessor.Descriptor.class);
        installLookupDataAdapter(MaxmindDataAdapter.NAME,
                MaxmindDataAdapter.class,
                MaxmindDataAdapter.Factory.class,
                MaxmindDataAdapter.Config.class);
    }
}
