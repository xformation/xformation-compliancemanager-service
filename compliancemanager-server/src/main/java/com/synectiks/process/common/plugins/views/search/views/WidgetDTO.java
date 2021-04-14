/*
 * */
package com.synectiks.process.common.plugins.views.search.views;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.search.engine.BackendQuery;
import com.synectiks.process.server.contentpacks.ContentPackable;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.EntityDescriptor;
import com.synectiks.process.server.contentpacks.model.entities.WidgetEntity;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AutoValue
@JsonDeserialize(builder = WidgetDTO.Builder.class)
@WithBeanGetter
public abstract class WidgetDTO implements ContentPackable<WidgetEntity> {
    public static final String FIELD_ID = "id";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_FILTER = "filter";
    public static final String FIELD_CONFIG = "config";
    public static final String FIELD_TIMERANGE = "timerange";
    public static final String FIELD_QUERY = "query";
    public static final String FIELD_STREAMS = "streams";

    @JsonProperty(FIELD_ID)
    public abstract String id();

    @JsonProperty(FIELD_TYPE)
    public abstract String type();

    @JsonProperty(FIELD_FILTER)
    @Nullable
    public abstract String filter();

    @JsonProperty(FIELD_TIMERANGE)
    public abstract Optional<TimeRange> timerange();

    @JsonProperty(FIELD_QUERY)
    public abstract Optional<BackendQuery> query();

    @JsonProperty(FIELD_STREAMS)
    public abstract Set<String> streams();

    @JsonProperty(FIELD_CONFIG)
    public abstract WidgetConfigDTO config();

    public static Builder builder() {
        return Builder.builder();
    };

    @AutoValue.Builder
    public static abstract class Builder {
        @JsonProperty(FIELD_ID)
        public abstract Builder id(String id);

        @JsonProperty(FIELD_TYPE)
        public abstract Builder type(String type);

        @JsonProperty(FIELD_FILTER)
        public abstract Builder filter(@Nullable String filter);

        @JsonProperty(FIELD_TIMERANGE)
        public abstract Builder timerange(@Nullable TimeRange timerange);

        @JsonProperty(FIELD_QUERY)
        public abstract Builder query(@Nullable BackendQuery query);

        @JsonProperty(FIELD_STREAMS)
        public abstract Builder streams(Set<String> streams);

        @JsonProperty(FIELD_CONFIG)
        @JsonTypeInfo(
                use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = WidgetDTO.FIELD_TYPE,
                visible = true,
                defaultImpl = UnknownWidgetConfigDTO.class)
        public abstract Builder config(WidgetConfigDTO config);

        public abstract WidgetDTO build();

        @JsonCreator
        static Builder builder() {
            return new AutoValue_WidgetDTO.Builder().streams(Collections.emptySet());
        }
    }

    @Override
    public WidgetEntity toContentPackEntity(EntityDescriptorIds entityDescriptorIds) {
        Set<String> mappedStreams = streams().stream().map(streamId ->
                entityDescriptorIds.get(EntityDescriptor.create(streamId, ModelTypes.STREAM_V1)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
        final WidgetEntity.Builder builder = WidgetEntity.builder()
                .id(this.id())
                .config(this.config())
                .filter(this.filter())
                .streams(mappedStreams)
                .type(this.type());
        if (this.query().isPresent()) {
            builder.query(this.query().get());
        }
        if (this.timerange().isPresent()) {
            builder.timerange(this.timerange().get());
        }
        return builder.build();
    }
}
