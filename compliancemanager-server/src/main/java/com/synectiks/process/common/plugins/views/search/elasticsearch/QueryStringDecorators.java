/*
 * */
package com.synectiks.process.common.plugins.views.search.elasticsearch;

import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.QueryResult;
import com.synectiks.process.common.plugins.views.search.SearchJob;
import com.synectiks.process.common.plugins.views.search.engine.QueryStringDecorator;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Set;

public class QueryStringDecorators implements QueryStringDecorator {
    private final Set<QueryStringDecorator> queryDecorators;

    public static class Fake extends QueryStringDecorators {
        public Fake() {
            super(Collections.emptySet());
        }
    }

    @Inject
    public QueryStringDecorators(Set<QueryStringDecorator> queryDecorators) {
        this.queryDecorators = queryDecorators;
    }

    @Override
    public String decorate(String queryString, SearchJob job, Query query, Set<QueryResult> results) {
        return this.queryDecorators.isEmpty() ? queryString : this.queryDecorators.stream()
                .reduce(queryString, (prev, decorator) -> decorator.decorate(prev, job, query, results), String::concat);
    }
}
