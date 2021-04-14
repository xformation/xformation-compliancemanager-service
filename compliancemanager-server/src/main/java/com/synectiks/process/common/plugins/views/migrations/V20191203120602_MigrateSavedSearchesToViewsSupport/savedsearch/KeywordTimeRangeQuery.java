/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.savedsearch;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.KeywordRange;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.TimeRange;

import javax.annotation.Nullable;
import java.util.Optional;

@AutoValue
@JsonAutoDetect
public abstract class KeywordTimeRangeQuery extends Query {
    public static final String type = "keyword";

    public abstract String keyword();

    @Override
    public TimeRange toTimeRange() {
        return KeywordRange.create(keyword());
    }

    @JsonCreator
    static KeywordTimeRangeQuery create(
            @JsonProperty("rangeType") String rangeType,
            @JsonProperty("fields") @Nullable String fields,
            @JsonProperty("query") String query,
            @JsonProperty("keyword") String keyword,
            @JsonProperty("streamId") @Nullable String streamId,
            @JsonProperty("interval") @Nullable String ignored
    ) {
        return new AutoValue_KeywordTimeRangeQuery(rangeType, Optional.ofNullable(fields), query, Optional.ofNullable(streamId), keyword);
    }
}
