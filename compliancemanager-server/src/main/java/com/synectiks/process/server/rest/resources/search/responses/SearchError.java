/*
 * */
package com.synectiks.process.server.rest.resources.search.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.rest.DetailedError;

import java.util.Collection;

@JsonAutoDetect
@AutoValue
public abstract class SearchError implements DetailedError {
    public static SearchError create(String message,
                                     Collection<String> details) {
        return new AutoValue_SearchError(message, details);
    }
}
