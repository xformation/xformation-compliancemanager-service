/*
 * */
package com.synectiks.process.common.grn;

import com.synectiks.process.server.plugin.PluginModule;

public class GRNModule extends PluginModule {
    @Override
    protected void configure() {
        bind(GRNRegistry.class).toInstance(GRNRegistry.createWithBuiltinTypes());
    }
}
