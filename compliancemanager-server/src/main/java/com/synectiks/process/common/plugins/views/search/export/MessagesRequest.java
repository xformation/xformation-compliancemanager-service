/*
 * */
package com.synectiks.process.common.plugins.views.search.export;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.search.elasticsearch.ElasticsearchQueryString;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import static com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand.DEFAULT_CHUNK_SIZE;
import static com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand.DEFAULT_FIELDS;
import static com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand.DEFAULT_QUERY;
import static com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand.DEFAULT_STREAMS;
import static com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand.defaultTimeRange;
import static com.synectiks.process.common.plugins.views.search.export.LinkedHashSetUtil.linkedHashSetOf;

import java.util.LinkedHashSet;
import java.util.OptionalInt;
import java.util.Set;

@JsonAutoDetect
@AutoValue
@JsonDeserialize(builder = MessagesRequest.Builder.class)
public abstract class MessagesRequest {
    private static final String FIELD_TIMERANGE = "timerange";
    private static final String FIELD_QUERY_STRING = "query_string";
    private static final String FIELD_FIELDS = "fields_in_order";
    private static final String FIELD_CHUNK_SIZE = "chunk_size";

    @JsonProperty(FIELD_TIMERANGE)
    public abstract TimeRange timeRange();

    @JsonProperty(FIELD_QUERY_STRING)
    public abstract ElasticsearchQueryString queryString();

    @JsonProperty
    public abstract Set<String> streams();

    @JsonProperty(FIELD_FIELDS)
    @NotEmpty
    public abstract LinkedHashSet<String> fieldsInOrder();

    @JsonProperty(FIELD_CHUNK_SIZE)
    public abstract int chunkSize();

    @JsonProperty
    @Positive
    public abstract OptionalInt limit();

    public static MessagesRequest withDefaults() {
        return builder().build();
    }

    public static Builder builder() {
        return Builder.create();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty(FIELD_TIMERANGE)
        public abstract Builder timeRange(TimeRange timeRange);

        @JsonProperty
        public abstract Builder streams(Set<String> streams);

        @JsonProperty(FIELD_QUERY_STRING)
        public abstract Builder queryString(ElasticsearchQueryString queryString);

        @JsonProperty(FIELD_FIELDS)
        public abstract Builder fieldsInOrder(LinkedHashSet<String> fieldsInOrder);

        public Builder fieldsInOrder(String... fieldsInOrder) {
            return fieldsInOrder(linkedHashSetOf(fieldsInOrder));
        }

        @JsonProperty(FIELD_CHUNK_SIZE)
        public abstract Builder chunkSize(int chunkSize);

        @JsonProperty
        public abstract Builder limit(Integer limit);

        abstract MessagesRequest autoBuild();

        public MessagesRequest build() {
            return autoBuild();
        }

        @JsonCreator
        public static Builder create() {
            return new AutoValue_MessagesRequest.Builder()
                    .timeRange(defaultTimeRange())
                    .streams(DEFAULT_STREAMS)
                    .queryString(DEFAULT_QUERY)
                    .fieldsInOrder(DEFAULT_FIELDS)
                    .chunkSize(DEFAULT_CHUNK_SIZE);
        }
    }
}
