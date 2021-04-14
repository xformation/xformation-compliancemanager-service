/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ElasticsearchQueryString {
    static final String NAME = "elasticsearch";

    @JsonProperty
    abstract String type();

    @JsonProperty
    abstract String queryString();

    public static ElasticsearchQueryString create(String query) {
        return new AutoValue_ElasticsearchQueryString(NAME, query);
    }
}
