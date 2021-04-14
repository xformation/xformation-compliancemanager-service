/*
 * */
package com.synectiks.process.common.plugins.views.search.errors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synectiks.process.common.plugins.views.search.Query;

import javax.annotation.Nonnull;

public class ResultWindowLimitError extends SearchTypeError {

    private final int resultWindowLimit;

    ResultWindowLimitError(@Nonnull Query query, @Nonnull String searchTypeId, int resultWindowLimit, Throwable throwable) {
        super(query, searchTypeId, throwable);

        this.resultWindowLimit = resultWindowLimit;
    }

    @JsonProperty("result_window_limit")
    public Integer getResultWindowLimit() {
        return resultWindowLimit;
    }
}
