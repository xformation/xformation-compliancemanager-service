/*
 * */
package com.synectiks.process.server.indexer.ranges;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.synectiks.process.server.indexer.ranges.MongoIndexRange;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MongoIndexRangeTest {
    @Test
    public void testCreate() throws Exception {
        String indexName = "test";
        DateTime begin = new DateTime(2015, 1, 1, 0, 0, DateTimeZone.UTC);
        DateTime end = new DateTime(2015, 2, 1, 0, 0, DateTimeZone.UTC);
        DateTime calculatedAt = new DateTime(2015, 2, 1, 0, 0, DateTimeZone.UTC);
        int calculationDuration = 42;
        MongoIndexRange indexRange = MongoIndexRange.create(indexName, begin, end, calculatedAt, calculationDuration);

        assertThat(indexRange.indexName()).isEqualTo(indexName);
        assertThat(indexRange.begin()).isEqualTo(begin);
        assertThat(indexRange.end()).isEqualTo(end);
        assertThat(indexRange.calculatedAt()).isEqualTo(calculatedAt);
        assertThat(indexRange.calculationDuration()).isEqualTo(calculationDuration);
    }

    @Test
    public void testJsonMapping() throws Exception {
        String indexName = "test";
        DateTime begin = new DateTime(2015, 1, 1, 0, 0, DateTimeZone.UTC);
        DateTime end = new DateTime(2015, 2, 1, 0, 0, DateTimeZone.UTC);
        DateTime calculatedAt = new DateTime(2015, 2, 1, 0, 0, DateTimeZone.UTC);
        int calculationDuration = 42;
        MongoIndexRange indexRange = MongoIndexRange.create(indexName, begin, end, calculatedAt, calculationDuration);

        ObjectMapper objectMapper = new ObjectMapperProvider().get();
        String json = objectMapper.writeValueAsString(indexRange);
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);

        assertThat((String) JsonPath.read(document, "$." + MongoIndexRange.FIELD_INDEX_NAME)).isEqualTo(indexName);
        assertThat((long) JsonPath.read(document, "$." + MongoIndexRange.FIELD_BEGIN)).isEqualTo(begin.getMillis());
        assertThat((long) JsonPath.read(document, "$." + MongoIndexRange.FIELD_END)).isEqualTo(end.getMillis());
        assertThat((long) JsonPath.read(document, "$." + MongoIndexRange.FIELD_CALCULATED_AT)).isEqualTo(calculatedAt.getMillis());
        assertThat((int) JsonPath.read(document, "$." + MongoIndexRange.FIELD_TOOK_MS)).isEqualTo(calculationDuration);
    }
}