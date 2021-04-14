/*
 * */
package com.synectiks.process.server.plugin.indexer.searches.timeranges;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.utilities.date.NaturalDateParser;

import org.joda.time.DateTime;

@AutoValue
@JsonTypeName(KeywordRange.KEYWORD)
public abstract class KeywordRange extends TimeRange {

    private static final NaturalDateParser DATE_PARSER = new NaturalDateParser();

    public static final String KEYWORD = "keyword";

    @JsonProperty
    @Override
    public abstract String type();

    @JsonProperty
    public abstract String keyword();

    private static NaturalDateParser.Result parseResult(String keyword) throws InvalidRangeParametersException {
        try {
            return DATE_PARSER.parse(keyword);
        } catch (NaturalDateParser.DateNotParsableException e) {
            throw new InvalidRangeParametersException("Could not parse from natural date: " + keyword);
        }
    }

    @JsonCreator
    public static KeywordRange create(@JsonProperty("type") String type, @JsonProperty("keyword") String keyword) throws InvalidRangeParametersException {
        return builder().type(type).keyword(keyword).build();
    }

    public static KeywordRange create(String keyword) throws InvalidRangeParametersException {
        return create(KEYWORD, keyword);
    }

    private static Builder builder() {
        return new AutoValue_KeywordRange.Builder();
    }

    public String getKeyword() {
        return keyword();
    }

    @JsonIgnore
    @Override
    public DateTime getFrom() {
        try {
            return parseResult(keyword()).getFrom();
        } catch (InvalidRangeParametersException e) {
            return null;
        }
    }

    @JsonIgnore
    @Override
    public DateTime getTo() {
        try {
            return parseResult(keyword()).getTo();
        } catch (InvalidRangeParametersException e) {
            return null;
        }
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder type(String type);

        public abstract Builder keyword(String keyword);

        abstract String keyword();

        abstract KeywordRange autoBuild();

        public KeywordRange build() throws InvalidRangeParametersException {
            parseResult(keyword());
            return autoBuild();
        }
    }
}

