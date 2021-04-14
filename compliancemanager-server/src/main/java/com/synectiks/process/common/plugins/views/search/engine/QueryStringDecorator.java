/*
 * */
package com.synectiks.process.common.plugins.views.search.engine;

import java.util.Set;

import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.QueryResult;
import com.synectiks.process.common.plugins.views.search.SearchJob;

public interface QueryStringDecorator {
    String decorate(String queryString, SearchJob job, Query query, Set<QueryResult> results);
}
