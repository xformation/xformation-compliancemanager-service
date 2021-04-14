/*
 * */
package com.synectiks.process.server.bindings;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.synectiks.process.server.filters.ExtractorFilter;
import com.synectiks.process.server.filters.StaticFieldFilter;
import com.synectiks.process.server.filters.StreamMatcherFilter;
import com.synectiks.process.server.plugin.filters.MessageFilter;

public class MessageFilterBindings extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<MessageFilter> messageFilters = Multibinder.newSetBinder(binder(), MessageFilter.class);
        messageFilters.addBinding().to(StaticFieldFilter.class);
        messageFilters.addBinding().to(ExtractorFilter.class);
        messageFilters.addBinding().to(StreamMatcherFilter.class);
    }
}
