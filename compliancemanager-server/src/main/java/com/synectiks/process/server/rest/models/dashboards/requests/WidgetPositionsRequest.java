/*
 * */
package com.synectiks.process.server.rest.models.dashboards.requests;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class WidgetPositionsRequest {
    @JsonProperty
    public abstract List<WidgetPosition> positions();

    @JsonCreator
    public static WidgetPositionsRequest create(@JsonProperty("positions") @NotEmpty List<WidgetPosition> positions) {
        return new AutoValue_WidgetPositionsRequest(positions);
    }

    @JsonAutoDetect
    @AutoValue
    @WithBeanGetter
    public abstract static class WidgetPosition {
        @JsonProperty
        public abstract String id();

        @JsonProperty
        public abstract int col();

        @JsonProperty
        public abstract int row();

        @JsonProperty
        public abstract int height();

        @JsonProperty
        public abstract int width();

        @JsonCreator
        public static WidgetPosition create(@JsonProperty("id") @NotEmpty String id,
                                            @JsonProperty("col") @Min(0) int col,
                                            @JsonProperty("row") @Min(0) int row,
                                            @JsonProperty("height") @Min(0) int height,
                                            @JsonProperty("width") @Min(0) int width) {
            return new AutoValue_WidgetPositionsRequest_WidgetPosition(id, col, row, height, width);
        }
    }
}
