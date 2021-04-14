/*
 * */
package com.synectiks.process.server.indexer.ranges;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.bson.types.ObjectId;
import org.graylog.autovalue.WithBeanGetter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongojack.Id;

import javax.annotation.Nullable;
import java.util.List;


@AutoValue
@WithBeanGetter
@JsonAutoDetect
public abstract class MongoIndexRange implements IndexRange {
    @Id
    @Nullable
    @JsonProperty("_id")
    public abstract ObjectId id();

    @JsonProperty(FIELD_INDEX_NAME)
    @Override
    public abstract String indexName();

    @Override
    public abstract DateTime begin();

    @Override
    public abstract DateTime end();

    @Override
    public abstract DateTime calculatedAt();

    @JsonProperty(FIELD_TOOK_MS)
    @Override
    public abstract int calculationDuration();

    @JsonProperty(FIELD_BEGIN)
    private long beginMillis() {
        return begin().getMillis();
    }

    @JsonProperty(FIELD_END)
    private long endMillis() {
        return end().getMillis();
    }

    @JsonProperty(FIELD_CALCULATED_AT)
    private long calculatedAtMillis() {
        return calculatedAt().getMillis();
    }

    @JsonProperty(FIELD_STREAM_IDS)
    @Override
    @Nullable
    public abstract List<String> streamIds();

    public static MongoIndexRange create(ObjectId id,
                                         String indexName,
                                         DateTime begin,
                                         DateTime end,
                                         DateTime calculatedAt,
                                         int calculationDuration,
                                         List<String> streamIds) {
        return new AutoValue_MongoIndexRange(id, indexName, begin, end, calculatedAt, calculationDuration, streamIds);
    }

    @JsonCreator
    public static MongoIndexRange create(@JsonProperty("_id") @Id @Nullable ObjectId id,
                                         @JsonProperty(FIELD_INDEX_NAME) String indexName,
                                         @JsonProperty(FIELD_BEGIN) long beginMillis,
                                         @JsonProperty(FIELD_END) long endMillis,
                                         @JsonProperty(FIELD_CALCULATED_AT) long calculatedAtMillis,
                                         @JsonProperty(FIELD_TOOK_MS) int calculationDuration,
                                         @JsonProperty(FIELD_STREAM_IDS) @Nullable List<String> streamIds) {
        final DateTime begin = new DateTime(beginMillis, DateTimeZone.UTC);
        final DateTime end = new DateTime(endMillis, DateTimeZone.UTC);
        final DateTime calculatedAt = new DateTime(calculatedAtMillis, DateTimeZone.UTC);
        return create(id, indexName, begin, end, calculatedAt, calculationDuration, streamIds);
    }

    public static MongoIndexRange create(String indexName,
                                         DateTime begin,
                                         DateTime end,
                                         DateTime calculatedAt,
                                         int calculationDuration,
                                         List<String> streamIds) {
        return create(null, indexName, begin, end, calculatedAt, calculationDuration, streamIds);
    }

    public static MongoIndexRange create(ObjectId id,
                                         String indexName,
                                         DateTime begin,
                                         DateTime end,
                                         DateTime calculatedAt,
                                         int calculationDuration) {
        return create(id, indexName, begin, end, calculatedAt, calculationDuration, null);
    }

    public static MongoIndexRange create(IndexRange indexRange) {
        return create(
                indexRange.indexName(),
                indexRange.begin(),
                indexRange.end(),
                indexRange.calculatedAt(),
                indexRange.calculationDuration(),
                indexRange.streamIds());
    }

    public static MongoIndexRange create(String indexName,
                                         DateTime begin,
                                         DateTime end,
                                         DateTime calculatedAt,
                                         int calculationDuration) {
        return create(null, indexName, begin, end, calculatedAt, calculationDuration, null);
    }
}