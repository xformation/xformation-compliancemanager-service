/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.savedsearch;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.RelativeRange;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.TimeRange;

import javax.annotation.Nullable;
import java.util.Optional;

@AutoValue
@JsonAutoDetect
public abstract class RelativeTimeRangeQuery extends Query {
    public static final String type = "relative";

    public abstract int relative();

    @Override
    public TimeRange toTimeRange() {
        return RelativeRange.create(relative());
    }

    @JsonCreator
    static RelativeTimeRangeQuery create(
            @JsonProperty("rangeType") String rangeType,
            @JsonProperty("fields") @Nullable String fields,
            @JsonProperty("query") String query,
            @JsonProperty("relative") int relative,
            @JsonProperty("streamId") @Nullable String streamId,
            @JsonProperty("interval") @Nullable String ignored
    ) {
        return new AutoValue_RelativeTimeRangeQuery(rangeType, Optional.ofNullable(fields), query, Optional.ofNullable(streamId), relative);
    }
}
