/*
 * */
package com.synectiks.process.server.inputs.transports;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static com.synectiks.process.server.inputs.transports.HttpPollTransport.parseHeaders;
import static org.junit.Assert.assertEquals;

/**
 * @author Lennart Koopmann <lennart@torch.sh>
 */
public class HttpPollTransportTest {

    @Test
    public void testParseHeaders() throws Exception {
        assertEquals(0, parseHeaders("").size());
        assertEquals(0, parseHeaders(" ").size());
        assertEquals(0, parseHeaders(" . ").size());
        assertEquals(0, parseHeaders("foo").size());
        assertEquals(1, parseHeaders("X-Foo: Bar").size());

        Map<String, String> expectedSingle = ImmutableMap.of("Accept", "application/json");
        Map<String, String> expectedMulti = ImmutableMap.of(
                "Accept", "application/json",
                "X-Foo", "bar");

        assertEquals(expectedMulti, parseHeaders("Accept: application/json, X-Foo: bar"));
        assertEquals(expectedSingle, parseHeaders("Accept: application/json"));

        assertEquals(expectedMulti, parseHeaders(" Accept:  application/json,X-Foo:bar"));
        assertEquals(expectedMulti, parseHeaders("Accept:application/json,   X-Foo: bar "));
        assertEquals(expectedMulti, parseHeaders("Accept:    application/json,     X-Foo: bar"));
        assertEquals(expectedMulti, parseHeaders("Accept :application/json,   X-Foo: bar "));

        assertEquals(expectedSingle, parseHeaders(" Accept: application/json"));
        assertEquals(expectedSingle, parseHeaders("Accept:application/json"));
        assertEquals(expectedSingle, parseHeaders(" Accept: application/json "));
        assertEquals(expectedSingle, parseHeaders(" Accept :application/json "));

    }

}
