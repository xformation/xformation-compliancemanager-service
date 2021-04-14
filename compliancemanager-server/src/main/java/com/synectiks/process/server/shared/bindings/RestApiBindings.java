/*
 * */
package com.synectiks.process.server.shared.bindings;

import com.google.inject.multibindings.Multibinder;
import com.synectiks.process.server.plugin.inject.Graylog2Module;
import com.synectiks.process.server.shared.security.ShiroSecurityBinding;
import com.synectiks.process.server.web.DevelopmentIndexHtmlGenerator;
import com.synectiks.process.server.web.IndexHtmlGenerator;
import com.synectiks.process.server.web.ProductionIndexHtmlGenerator;

import javax.ws.rs.container.DynamicFeature;

public class RestApiBindings extends Graylog2Module {
    @Override
    protected void configure() {
        bindDynamicFeatures();
        bindContainerResponseFilters();
        // just to create the binders so they are present in the injector
        // we don't actually have global REST API bindings for these
        jerseyExceptionMapperBinder();
        jerseyAdditionalComponentsBinder();

        // In development mode we use an external process to provide the web interface.
        // To avoid errors because of missing production web assets, we use a different implementation for
        // generating the "index.html" page.
        final String development = System.getenv("DEVELOPMENT");
        if (development == null || development.equalsIgnoreCase("false")) {
            bind(IndexHtmlGenerator.class).to(ProductionIndexHtmlGenerator.class).asEagerSingleton();
        } else {
            bind(IndexHtmlGenerator.class).to(DevelopmentIndexHtmlGenerator.class).asEagerSingleton();
        }
    }

    private void bindDynamicFeatures() {
        Multibinder<Class<? extends DynamicFeature>> setBinder = jerseyDynamicFeatureBinder();
        setBinder.addBinding().toInstance(ShiroSecurityBinding.class);
    }

    private void bindContainerResponseFilters() {
        jerseyContainerResponseFilterBinder();
    }

}
