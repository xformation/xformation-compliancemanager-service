/*
 * */
package com.synectiks.process.server.plugin.decorators;

import com.synectiks.process.server.decorators.Decorator;
import com.synectiks.process.server.plugin.DescriptorWithHumanName;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.rest.resources.search.responses.SearchResponse;

import java.util.function.Function;

@FunctionalInterface
public interface SearchResponseDecorator extends Function<SearchResponse, SearchResponse> {
    interface Factory {
        SearchResponseDecorator create(Decorator decorator);
        Config getConfig();
        Descriptor getDescriptor();
    }

    interface Config {
        ConfigurationRequest getRequestedConfiguration();
    }

    abstract class Descriptor extends DescriptorWithHumanName {
        public Descriptor(String name, String linkToDocs, String humanName) {
            super(name, false, linkToDocs, humanName);
        }
    }
}
