/*
 * */
package com.synectiks.process.common.plugins.views.search.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.plugins.views.search.Query;

import org.apache.commons.lang.exception.ExceptionUtils;

import static com.synectiks.process.server.shared.utilities.ExceptionUtils.getRootCauseMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class QueryError implements SearchError {

    private final Query query;

    @Nullable
    private final Throwable throwable;

    private final String description;

    public QueryError(@Nonnull Query query, Throwable throwable) {
        this.query = query;
        this.throwable = throwable;
        this.description = getRootCauseMessage(throwable);
    }

    public QueryError(@Nonnull Query query, String description) {
        this.query = query;
        this.description = description;
        this.throwable = null;
    }

    @JsonProperty("query_id")
    public String queryId() {
        return query.id();
    }

    @Override
    public String description() {
        return description;
    }

    @Nullable
    @JsonProperty("backtrace")
    public String backtrace() {
        if (throwable == null) {
            return null;
        }
        return ExceptionUtils.getFullStackTrace(throwable);
    }
}
