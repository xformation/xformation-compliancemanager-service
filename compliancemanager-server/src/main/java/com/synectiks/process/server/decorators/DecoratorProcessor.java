/*
 * */
package com.synectiks.process.server.decorators;

import com.google.inject.ImplementedBy;
import com.synectiks.process.server.plugin.decorators.SearchResponseDecorator;
import com.synectiks.process.server.rest.resources.search.responses.SearchResponse;

import java.util.List;
import java.util.Optional;

@ImplementedBy(DecoratorProcessorImpl.class)
public interface DecoratorProcessor {
    SearchResponse decorate(SearchResponse searchResponse, Optional<String> stream);
    SearchResponse decorate(SearchResponse searchResponse, List<SearchResponseDecorator> searchResponseDecorators);

    class Fake implements DecoratorProcessor{
        @Override
        public SearchResponse decorate(SearchResponse searchResponse, Optional<String> stream) {
            return searchResponse;
        }

        @Override
        public SearchResponse decorate(SearchResponse searchResponse, List<SearchResponseDecorator> searchResponseDecorators) {
            return searchResponse;
        }
    }
}
