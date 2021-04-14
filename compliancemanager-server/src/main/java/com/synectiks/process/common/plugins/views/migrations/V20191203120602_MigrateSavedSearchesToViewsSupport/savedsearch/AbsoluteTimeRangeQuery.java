/*
 * */
package com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.savedsearch;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.AbsoluteRange;
import com.synectiks.process.common.plugins.views.migrations.V20191203120602_MigrateSavedSearchesToViewsSupport.view.TimeRange;

import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.util.Optional;

@AutoValue
@JsonAutoDetect
public abstract class AbsoluteTimeRangeQuery extends Query {
    public static final String type = "absolute";

    public abstract DateTime from();
    public abstract DateTime to();

    @Override
    public TimeRange toTimeRange() {
        return AbsoluteRange.create(from(), to());
    }

    @JsonCreator
    static AbsoluteTimeRangeQuery create(
            @JsonProperty("rangeType") String rangeType,
            @JsonProperty("fields") @Nullable String fields,
            @JsonProperty("query") String query,
            @JsonProperty("from") DateTime from,
            @JsonProperty("to") DateTime to,
            @JsonProperty("streamId") @Nullable String streamId,
            @JsonProperty("interval") @Nullable String ignored
    ) {
        return new AutoValue_AbsoluteTimeRangeQuery(rangeType, Optional.ofNullable(fields), query, Optional.ofNullable(streamId), from, to);
    }
}
