/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.Traverser;
import com.synectiks.process.common.plugins.views.search.Filter;
import com.synectiks.process.common.plugins.views.search.GlobalOverride;
import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.engine.BackendQuery;
import com.synectiks.process.common.plugins.views.search.filter.StreamFilter;
import com.synectiks.process.server.contentpacks.NativeEntityConverter;
import com.synectiks.process.server.contentpacks.exceptions.ContentPackException;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;
import com.synectiks.process.server.plugin.streams.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.collect.ImmutableSortedSet.of;
import static java.util.stream.Collectors.toSet;

@AutoValue
@JsonAutoDetect
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = QueryEntity.Builder.class)
public abstract class QueryEntity implements NativeEntityConverter<Query> {

    @JsonProperty
    public abstract String id();

    @JsonProperty
    public abstract TimeRange timerange();

    @Nullable
    @JsonProperty
    public abstract Filter filter();

    @Nonnull
    @JsonProperty
    public abstract BackendQuery query();

    @JsonIgnore
    public abstract Optional<GlobalOverride> globalOverride();

    @Nonnull
    @JsonProperty("search_types")
    public abstract ImmutableSet<SearchTypeEntity> searchTypes();

    public Set<String> usedStreamIds() {
        return Optional.ofNullable(filter())
                .map(optFilter -> {
                    @SuppressWarnings("UnstableApiUsage") final Traverser<Filter> filterTraverser = Traverser.forTree(filter -> firstNonNull(filter.filters(), Collections.emptySet()));
                    return StreamSupport.stream(filterTraverser.breadthFirst(optFilter).spliterator(), false)
                            .filter(filter -> filter instanceof StreamFilter)
                            .map(streamFilter -> ((StreamFilter) streamFilter).streamId())
                            .filter(Objects::nonNull)
                            .collect(toSet());
                })
                .orElse(Collections.emptySet());
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return Builder.createWithDefaults();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        @JsonProperty
        public abstract Builder id(String id);

        @JsonProperty
        public abstract Builder timerange(TimeRange timerange);

        @JsonProperty
        public abstract Builder filter(Filter filter);

        @JsonProperty
        public abstract Builder query(BackendQuery query);

        public abstract Builder globalOverride(@Nullable GlobalOverride globalOverride);

        @JsonProperty("search_types")
        public abstract Builder searchTypes(@Nullable Set<SearchTypeEntity> searchTypes);

        abstract QueryEntity autoBuild();

        @JsonCreator
        static Builder createWithDefaults() {
            return new AutoValue_QueryEntity.Builder().searchTypes(of());
        }

        public QueryEntity build() {
            return autoBuild();
        }
    }

    // TODO: This code assumes that we only use shallow filters for streams.
    //       If this ever changes, we need to implement a mapper that can handle filter trees.
    private Filter shallowMappedFilter(Map<EntityDescriptor, Object> nativeEntities) {
       return Optional.ofNullable(filter())
               .map(optFilter -> {
                  Set<Filter> newFilters = optFilter.filters().stream()
                          .map(filter -> {
                              if (filter.type().matches(StreamFilter.NAME)) {
                                  final StreamFilter streamFilter = (StreamFilter) filter;
                                  final Stream stream = (Stream) nativeEntities.get(
                                          EntityDescriptor.create(streamFilter.streamId(), ModelTypes.STREAM_V1));
                                  if (Objects.isNull(stream)) {
                                      throw new ContentPackException("Could not find matching stream id: " +
                                              streamFilter.streamId());
                                  }
                                  return streamFilter.toBuilder().streamId(stream.getId()).build();
                              }
                              return filter;
                          }).collect(Collectors.toSet());
                  return optFilter.toGenericBuilder().filters(newFilters).build();
               })
               .orElse(null);
    }

    @Override
    public Query toNativeEntity(Map<String, ValueReference> parameters, Map<EntityDescriptor, Object> nativeEntities) {
        return Query.builder()
                .id(id())
                .searchTypes(searchTypes().stream().map(s -> s.toNativeEntity(parameters, nativeEntities))
                        .collect(Collectors.toSet()))
                .query(query())
                .filter(shallowMappedFilter(nativeEntities))
                .timerange(timerange())
                .globalOverride(globalOverride().orElse(null))
                .build();
    }
}
