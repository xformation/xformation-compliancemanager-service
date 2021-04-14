/*
 * */
package com.synectiks.process.server.rest.models.tools.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class RegexReplaceTesterResponse {
    @JsonProperty
    public abstract boolean matched();

    @JsonProperty
    @Nullable
    public abstract Match match();

    @JsonProperty
    public abstract String regex();

    @JsonProperty
    public abstract String replacement();

    @JsonProperty("replace_all")
    public abstract boolean replaceAll();

    @JsonProperty
    public abstract String string();

    @JsonCreator
    public static RegexReplaceTesterResponse create(@JsonProperty("matched") boolean matched,
                                                    @JsonProperty("match") @Nullable Match match,
                                                    @JsonProperty("regex") String regex,
                                                    @JsonProperty("replacement") String replacement,
                                                    @JsonProperty("replace_all") boolean replaceAll,
                                                    @JsonProperty("string") String string) {
        return new AutoValue_RegexReplaceTesterResponse(matched, match, regex, replacement, replaceAll, string);
    }

    @JsonAutoDetect
    @AutoValue
    @WithBeanGetter
    public static abstract class Match {
        @JsonProperty
        public abstract String match();

        @JsonProperty
        public abstract int start();

        @JsonProperty
        public abstract int end();

        @JsonCreator
        public static Match create(@JsonProperty("match") String match,
                                   @JsonProperty("start") int start,
                                   @JsonProperty("end") int end) {
            return new AutoValue_RegexReplaceTesterResponse_Match(match, start, end);
        }
    }
}
