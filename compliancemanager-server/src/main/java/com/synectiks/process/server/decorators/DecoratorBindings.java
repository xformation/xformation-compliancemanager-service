/*
 * */
package com.synectiks.process.server.decorators;

import com.google.inject.multibindings.MapBinder;
import com.synectiks.process.server.plugin.decorators.SearchResponseDecorator;
import com.synectiks.process.server.plugin.inject.Graylog2Module;

public class DecoratorBindings extends Graylog2Module {
    @Override
    protected void configure() {
        final MapBinder<String, SearchResponseDecorator.Factory> searchResponseDecoratorBinder = searchResponseDecoratorBinder();
        installSearchResponseDecorator(searchResponseDecoratorBinder,
                                       SyslogSeverityMapperDecorator.class,
                                       SyslogSeverityMapperDecorator.Factory.class);

        installSearchResponseDecorator(searchResponseDecoratorBinder,
                                       FormatStringDecorator.class,
                                       FormatStringDecorator.Factory.class);

        installSearchResponseDecorator(searchResponseDecoratorBinder,
                                       LookupTableDecorator.class,
                                       LookupTableDecorator.Factory.class);

        installSearchResponseDecorator(searchResponseDecoratorBinder,
                                       LinkFieldDecorator.class,
                                       LinkFieldDecorator.Factory.class);
    }
}
