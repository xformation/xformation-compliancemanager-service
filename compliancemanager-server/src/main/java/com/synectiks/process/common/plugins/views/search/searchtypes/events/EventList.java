/*
 * */
package com.synectiks.process.common.plugins.views.search.searchtypes.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import com.synectiks.process.common.plugins.views.search.Filter;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.common.plugins.views.search.engine.BackendQuery;
import com.synectiks.process.common.plugins.views.search.timeranges.DerivedTimeRange;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.model.entities.EventListEntity;
import com.synectiks.process.server.contentpacks.model.entities.SearchTypeEntity;

import javax.annotation.Nullable;

import static com.synectiks.process.server.plugin.streams.Stream.DEFAULT_EVENTS_STREAM_ID;
import static com.synectiks.process.server.plugin.streams.Stream.DEFAULT_SYSTEM_EVENTS_STREAM_ID;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@AutoValue
@JsonTypeName(EventList.NAME)
@JsonDeserialize(builder = EventList.Builder.class)
public abstract class EventList implements SearchType {
    public static final String NAME = "events";

    @Override
    public abstract String type();

    @Override
    @Nullable
    @JsonProperty
    public abstract String id();

    @Nullable
    @Override
    public abstract Filter filter();

    @JsonCreator
    public static Builder builder() {
        return new AutoValue_EventList.Builder()
                .type(NAME)
                .streams(Collections.emptySet());
    }

    public abstract Builder toBuilder();

    @Override
    public SearchType applyExecutionContext(ObjectMapper objectMapper, JsonNode state) {
        return this;
    }

    @Override
    public Set<String> effectiveStreams() {
        return ImmutableSet.of(DEFAULT_EVENTS_STREAM_ID, DEFAULT_SYSTEM_EVENTS_STREAM_ID);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonCreator
        public static Builder createDefault() {
            return builder()
                    .streams(Collections.emptySet());
        }

        @JsonProperty
        public abstract Builder type(String type);

        @JsonProperty
        public abstract Builder id(String id);

        @JsonProperty
        public abstract Builder name(@Nullable String name);

        @JsonProperty
        public abstract Builder filter(@Nullable Filter filter);

        @JsonProperty
        public abstract Builder query(@Nullable BackendQuery query);

        @JsonProperty
        public abstract Builder timerange(@Nullable DerivedTimeRange timeRange);

        @JsonProperty
        public abstract Builder streams(Set<String> streams);

        public abstract EventList build();
    }

    @AutoValue
    public abstract static class Result implements SearchType.Result {
        @Override
        @JsonProperty
        public abstract String id();

        @Override
        @JsonProperty
        public String type() {
            return NAME;
        }

        @JsonProperty
        public abstract List<EventSummary> events();

        public static Builder builder() {
            return new AutoValue_EventList_Result.Builder();
        }

        public static Builder result(String searchTypeId) {
            return builder().id(searchTypeId);
        }

        @AutoValue.Builder
        public abstract static class Builder {
            public abstract Builder id(String id);

            public abstract Builder name(String name);

            public abstract Builder events(List<EventSummary> events);

            public abstract Result build();
        }
    }

    @Override
    public SearchTypeEntity toContentPackEntity(EntityDescriptorIds entityDescriptorIds) {
        return EventListEntity.builder()
                .streams(mappedStreams(entityDescriptorIds))
                .filter(filter())
                .id(id())
                .name(name().orElse(null))
                .query(query().orElse(null))
                .type(type())
                .timerange(timerange().orElse(null))
                .build();
    }
}
