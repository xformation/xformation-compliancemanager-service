/*
 * */
package com.synectiks.process.common.plugins.views.search.searchtypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.search.Filter;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.common.plugins.views.search.engine.BackendQuery;
import com.synectiks.process.common.plugins.views.search.timeranges.DerivedTimeRange;
import com.synectiks.process.common.plugins.views.search.timeranges.OffsetRange;
import com.synectiks.process.server.contentpacks.EntityDescriptorIds;
import com.synectiks.process.server.contentpacks.model.entities.MessageListEntity;
import com.synectiks.process.server.contentpacks.model.entities.SearchTypeEntity;
import com.synectiks.process.server.decorators.Decorator;
import com.synectiks.process.server.decorators.DecoratorImpl;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.KeywordRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.RelativeRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;
import com.synectiks.process.server.rest.models.messages.responses.DecorationStats;
import com.synectiks.process.server.rest.models.messages.responses.ResultMessageSummary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@AutoValue
@JsonTypeName(MessageList.NAME)
@JsonDeserialize(builder = MessageList.Builder.class)
public abstract class MessageList implements SearchType {
    public static final String NAME = "messages";

    @Override
    public abstract String type();

    @Override
    @Nullable
    @JsonProperty
    public abstract String id();

    @JsonProperty
    public abstract Optional<String> name();

    @Nullable
    @Override
    public abstract Filter filter();

    @JsonProperty
    public abstract int limit();

    @JsonProperty
    public abstract int offset();

    @Nullable
    @JsonProperty
    public abstract List<Sort> sort();

    @JsonProperty
    public abstract List<Decorator> decorators();

    @JsonCreator
    public static Builder builder() {
        return new AutoValue_MessageList.Builder()
                .type(NAME)
                .limit(150)
                .offset(0)
                .streams(Collections.emptySet())
                .decorators(Collections.emptyList());
    }

    public abstract Builder toBuilder();

    @Override
    public SearchType applyExecutionContext(ObjectMapper objectMapper, JsonNode state) {
        final boolean hasLimit = state.hasNonNull("limit");
        final boolean hasOffset = state.hasNonNull("offset");
        if (hasLimit || hasOffset) {
            final Builder builder = toBuilder();
            if (hasLimit) {
                builder.limit(state.path("limit").asInt());
            }
            if (hasOffset) {
                builder.offset(state.path("offset").asInt());
            }
            return builder.build();
        }
        return this;
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
        public abstract Builder id(@Nullable String id);

        @JsonProperty
        public abstract Builder name(@Nullable String name);

        @JsonProperty
        public abstract Builder filter(@Nullable Filter filter);

        @JsonProperty
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
        @JsonSubTypes({
                @JsonSubTypes.Type(name = AbsoluteRange.ABSOLUTE, value = AbsoluteRange.class),
                @JsonSubTypes.Type(name = RelativeRange.RELATIVE, value = RelativeRange.class),
                @JsonSubTypes.Type(name = KeywordRange.KEYWORD, value = KeywordRange.class),
                @JsonSubTypes.Type(name = OffsetRange.OFFSET, value = OffsetRange.class)
        })
        public Builder timerange(@Nullable TimeRange timerange) {
            return timerange(timerange == null ? null : DerivedTimeRange.of(timerange));
        }
        public abstract Builder timerange(@Nullable DerivedTimeRange timerange);

        @JsonProperty
        public abstract Builder query(@Nullable BackendQuery query);

        @JsonProperty
        public abstract Builder streams(Set<String> streams);

        @JsonProperty
        public abstract Builder limit(int limit);

        @JsonProperty
        public abstract Builder offset(int offset);

        @JsonProperty
        public abstract Builder sort(@Nullable List<Sort> sort);

        @JsonProperty("decorators")
        public Builder _decorators(List<DecoratorImpl> decorators) {
            return decorators(new ArrayList<>(decorators));
        }

        public abstract Builder decorators(List<Decorator> decorators);

        public abstract MessageList build();
    }

    @AutoValue
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
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
        public abstract List<ResultMessageSummary> messages();

        @JsonProperty
        public abstract Optional<DecorationStats> decorationStats();

        @JsonProperty
        public abstract AbsoluteRange effectiveTimerange();

        @JsonProperty
        public abstract long totalResults();

        public static Builder builder() {
            return new AutoValue_MessageList_Result.Builder();
        }

        public static Builder result(String searchTypeId) {
            return builder().id(searchTypeId);
        }

        @AutoValue.Builder
        public abstract static class Builder {
            public abstract Builder id(String id);

            public abstract Builder name(@Nullable String name);

            public abstract Builder messages(List<ResultMessageSummary> messages);

            public abstract Builder totalResults(long totalResults);

            public abstract Builder decorationStats(DecorationStats decorationStats);

            public abstract Builder effectiveTimerange(AbsoluteRange effectiveTimerange);

            public abstract Result build();
        }
    }

    @Override
    public SearchTypeEntity toContentPackEntity(EntityDescriptorIds entityDescriptorIds) {
        return MessageListEntity.builder()
                .decorators(decorators())
                .streams(mappedStreams(entityDescriptorIds))
                .timerange(timerange().orElse(null))
                .limit(limit())
                .offset(offset())
                .filter(filter())
                .id(id())
                .name(name().orElse(null))
                .query(query().orElse(null))
                .type(type())
                .sort(sort())
                .build();
    }
}
