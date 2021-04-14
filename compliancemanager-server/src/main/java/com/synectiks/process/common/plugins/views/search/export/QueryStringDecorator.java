/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.Search;
import com.synectiks.process.common.plugins.views.search.SearchJob;
import com.synectiks.process.common.plugins.views.search.elasticsearch.QueryStringDecorators;

import javax.inject.Inject;
import java.util.UUID;

public class QueryStringDecorator {

    private final QueryStringDecorators decorator;

    @Inject
    public QueryStringDecorator(QueryStringDecorators decorator) {
        this.decorator = decorator;
    }

    public String decorateQueryString(String queryString, Search search, Query query) {

        SearchJob jobStub = new SearchJob(UUID.randomUUID().toString(), search, "views backend");

        return decorator.decorate(queryString, jobStub, query, ImmutableSet.of());
    }
}
