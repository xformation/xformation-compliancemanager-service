/*
 * */
package com.synectiks.process.server.rest.models.search.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class TermsResult {
    @JsonProperty
    public abstract long time();

    @JsonProperty
    public abstract Map<String, Long> terms();

    @JsonProperty
    public abstract Map<String, List<Map<String, String>>> termsMapping();

    @JsonProperty
    public abstract long missing();

    @JsonProperty
    public abstract long other();

    @JsonProperty
    public abstract long total();

    @JsonProperty
    public abstract String builtQuery();

    public static TermsResult create(long time,
                                     Map<String, Long> terms,
                                     long missing,
                                     long other,
                                     long total,
                                     String builtQuery) {
        return create(time, terms, Collections.emptyMap(), missing, other, total, builtQuery);
    }

    public static TermsResult create(long time,
                                     Map<String, Long> terms,
                                     Map<String, List<Map<String, String>>> termsMapping,
                                     long missing,
                                     long other,
                                     long total,
                                     String builtQuery) {
        return new AutoValue_TermsResult(time, terms, termsMapping, missing, other, total, builtQuery);
    }
}
