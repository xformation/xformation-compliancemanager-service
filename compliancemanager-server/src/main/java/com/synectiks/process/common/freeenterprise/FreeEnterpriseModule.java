/*
 * */
package com.synectiks.process.common.freeenterprise;

import com.synectiks.process.server.plugin.PluginModule;

public class FreeEnterpriseModule extends PluginModule {
    @Override
    protected void configure() {
        // Add all rest resources in this package
        registerRestControllerPackage(this.getClass().getPackage().getName());
    }
}
