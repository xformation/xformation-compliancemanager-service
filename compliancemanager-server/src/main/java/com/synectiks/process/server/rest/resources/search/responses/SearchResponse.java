/*
 * */
package com.synectiks.process.server.rest.resources.search.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.rest.models.messages.responses.ResultMessageSummary;
import com.synectiks.process.server.rest.models.system.indexer.responses.IndexRangeSummary;

import org.graylog.autovalue.WithBeanGetter;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class SearchResponse {
    @JsonProperty
    public abstract String query();

    @JsonProperty
    public abstract String builtQuery();

    @JsonProperty
    public abstract Set<IndexRangeSummary> usedIndices();

    @JsonProperty
    public abstract List<ResultMessageSummary> messages();

    @JsonProperty
    public abstract Set<String> fields();

    @JsonProperty
    public abstract long time();

    @JsonProperty
    public abstract long totalResults();

    @JsonProperty
    public abstract DateTime from();

    @JsonProperty
    public abstract DateTime to();

    @JsonProperty
    @Nullable
    public abstract SearchDecorationStats decorationStats();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_SearchResponse.Builder();
    }

    public static SearchResponse create(String query,
                                        String builtQuery,
                                        Set<IndexRangeSummary> usedIndices,
                                        List<ResultMessageSummary> messages,
                                        Set<String> fields,
                                        long time,
                                        long totalResults,
                                        DateTime from,
                                        DateTime to) {
        return builder()
            .query(query)
            .builtQuery(builtQuery)
            .usedIndices(usedIndices)
            .messages(messages)
            .fields(fields)
            .time(time)
            .totalResults(totalResults)
            .from(from)
            .to(to)
            .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder query(String query);
        public abstract Builder builtQuery(String builtQuery);
        public abstract Builder usedIndices(Set<IndexRangeSummary> usedIndices);
        public abstract Builder messages(List<ResultMessageSummary> messages);
        public abstract Builder fields(Set<String> fields);
        public abstract Builder time(long time);
        public abstract Builder totalResults(long totalResults);
        public abstract Builder from(DateTime from);
        public abstract Builder to(DateTime to);
        public abstract Builder decorationStats(SearchDecorationStats searchDecorationStats);
        public abstract SearchResponse build();
    }
}
