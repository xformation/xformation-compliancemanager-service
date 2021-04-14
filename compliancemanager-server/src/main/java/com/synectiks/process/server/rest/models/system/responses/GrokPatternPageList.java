/*
 * */
package com.synectiks.process.server.rest.models.system.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.database.PaginatedList;
import com.synectiks.process.server.grok.GrokPattern;

import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import java.util.Collection;

@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class GrokPatternPageList {
    @Nullable
    @JsonProperty
    public abstract String query();


    @JsonProperty("pagination")
    public abstract PaginatedList.PaginationInfo paginationInfo();

    @JsonProperty
    public abstract long total();

    @Nullable
    @JsonProperty
    public abstract String sort();

    @Nullable
    @JsonProperty
    public abstract String order();

    @JsonProperty
    public abstract Collection<GrokPattern> patterns();


    @JsonCreator
    public static GrokPatternPageList create(
            @JsonProperty("query") @Nullable String query,
            @JsonProperty("pagination") PaginatedList.PaginationInfo paginationInfo,
            @JsonProperty("total") long total,
            @JsonProperty("sort") @Nullable String sort,
            @JsonProperty("order") @Nullable String order,
            @JsonProperty("patterns") Collection<GrokPattern> patternList) {
        return new AutoValue_GrokPatternPageList(query, paginationInfo, total, sort, order, patternList);
    }
}
