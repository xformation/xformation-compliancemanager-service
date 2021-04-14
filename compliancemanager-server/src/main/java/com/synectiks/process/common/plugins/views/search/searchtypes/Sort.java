/*
 * */
package com.synectiks.process.common.plugins.views.search.searchtypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Sort {
    public enum Order {
        ASC,
        DESC
    }

    @JsonProperty
    public abstract String field();

    @JsonProperty
    public abstract Order order();

    @JsonCreator
    public static Sort create(@JsonProperty("field") String field, @JsonProperty("order") Order order) {
        return new AutoValue_Sort(field, order);
    }

}
