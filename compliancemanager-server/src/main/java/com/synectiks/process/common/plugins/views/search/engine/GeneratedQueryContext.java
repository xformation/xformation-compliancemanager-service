/*
 * */
package com.synectiks.process.common.plugins.views.search.engine;

import java.util.Collection;

import com.synectiks.process.common.plugins.views.search.errors.SearchError;

public interface GeneratedQueryContext {

    void addError(SearchError error);

    Collection<SearchError> errors();
}
