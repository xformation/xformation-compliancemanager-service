/*
 * */
package com.synectiks.process.server.plugin.indexer.searches.timeranges;

import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.InvalidRangeParametersException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AbsoluteRangeEntityTest {
    @Test
    public void testStringParse() throws Exception {
        final AbsoluteRange range1 = AbsoluteRange.create("2016-03-24T00:00:00.000Z", "2016-03-24T23:59:59.000Z");

        assertThat(range1.from().toString(ISODateTimeFormat.dateTime()))
                .isEqualTo("2016-03-24T00:00:00.000Z");
        assertThat(range1.to().toString(ISODateTimeFormat.dateTime()))
                .isEqualTo("2016-03-24T23:59:59.000Z");

        final AbsoluteRange range2 = AbsoluteRange.create("2016-03-24T00:00:00.000+09:00", "2016-03-24T23:59:59.000+09:00");

        // Check that time zone is kept while parsing.
        assertThat(range2.from().toString(ISODateTimeFormat.dateTime()))
                .isEqualTo("2016-03-24T00:00:00.000+09:00");
        assertThat(range2.to().toString(ISODateTimeFormat.dateTime()))
                .isEqualTo("2016-03-24T23:59:59.000+09:00");
    }

    @Test
    public void nullOrEmptyStringsThrowException() {
        assertThatThrownBy(() -> AbsoluteRange.create(null, "2018-07-11T14:32:00.000Z"))
                .isInstanceOf(InvalidRangeParametersException.class)
                .hasMessage("Invalid start of range: <null>");
        assertThatThrownBy(() -> AbsoluteRange.create("", "2018-07-11T14:32:00.000Z"))
                .isInstanceOf(InvalidRangeParametersException.class)
                .hasMessage("Invalid start of range: <>");
        assertThatThrownBy(() -> AbsoluteRange.create("2018-07-11T14:32:00.000Z", null))
                .isInstanceOf(InvalidRangeParametersException.class)
                .hasMessage("Invalid end of range: <null>");
        assertThatThrownBy(() -> AbsoluteRange.create("2018-07-11T14:32:00.000Z", ""))
                .isInstanceOf(InvalidRangeParametersException.class)
                .hasMessage("Invalid end of range: <>");
    }
}