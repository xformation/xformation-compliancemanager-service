/*
 * */
package com.synectiks.process.common.plugins.views.search.errors;

import javax.annotation.Nonnull;

import com.synectiks.process.common.plugins.views.search.Query;

public class EmptyParameterError extends QueryError {
    public EmptyParameterError(@Nonnull Query query, String description) {
        super(query, description);
    }
}
