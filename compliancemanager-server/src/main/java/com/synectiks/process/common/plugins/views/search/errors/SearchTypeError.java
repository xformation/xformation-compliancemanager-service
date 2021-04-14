/*
 * */
package com.synectiks.process.common.plugins.views.search.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.plugins.views.search.Query;

import javax.annotation.Nonnull;

public class SearchTypeError extends QueryError {
    @Nonnull
    private final String searchTypeId;

    public SearchTypeError(@Nonnull Query query, @Nonnull String searchTypeId, Throwable throwable) {
        super(query, throwable);

        this.searchTypeId = searchTypeId;
    }

    public SearchTypeError(@Nonnull Query query, @Nonnull String searchTypeId, String description) {
        super(query, description);

        this.searchTypeId = searchTypeId;
    }

    @Nonnull
    @JsonProperty("search_type_id")
    public String searchTypeId() {
        return searchTypeId;
    }
}
