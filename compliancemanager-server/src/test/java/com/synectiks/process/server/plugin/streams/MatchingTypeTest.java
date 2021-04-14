/*
 * */
package com.synectiks.process.server.plugin.streams;

import org.junit.Test;

import com.synectiks.process.server.plugin.streams.Stream;

import static org.junit.Assert.assertEquals;

public class MatchingTypeTest {

    @Test
    public void testValueOfOrDefault() throws Exception {
        assertEquals(Stream.MatchingType.AND, Stream.MatchingType.valueOfOrDefault("AND"));
        assertEquals(Stream.MatchingType.OR, Stream.MatchingType.valueOfOrDefault("OR"));
        assertEquals(Stream.MatchingType.DEFAULT, Stream.MatchingType.valueOfOrDefault(null));
        assertEquals(Stream.MatchingType.DEFAULT, Stream.MatchingType.valueOfOrDefault(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfOrDefaultThrowsExceptionForUnknownEnumName() {
        Stream.MatchingType.valueOfOrDefault("FOO");
    }
}