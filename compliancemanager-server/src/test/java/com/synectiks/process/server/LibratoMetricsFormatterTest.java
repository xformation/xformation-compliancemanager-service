/*
 * */
package com.synectiks.process.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class LibratoMetricsFormatterTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testAsJson() throws IOException {

        // TODO

        /*MessageCounterImpl counter = new MessageCounterImpl();

        // Total: 2
        counter.incrementTotal();
        counter.incrementTotal();

        // Host foo.example.org: 3
        counter.incrementSource("foo.example.org");
        counter.incrementSource("foo.example.org");
        counter.incrementSource("foo.example.org");

        // Host bar.example.org: 1
        counter.incrementSource("bar.example.org");

        Map<String, String> fakeStreamNames = Maps.newHashMap();
        
        // Stream id1: 2
        ObjectId id1 = new ObjectId();
        fakeStreamNames.put(id1.toString(), "lol-stream1");
        counter.incrementStream(id1);
        counter.incrementStream(id1);

        // Stream id2: 1
        ObjectId id2 = new ObjectId();
        fakeStreamNames.put(id2.toString(), "lolano$therSTREAM");
        counter.incrementStream(id2);

        LibratoMetricsFormatter f = new LibratoMetricsFormatter(counter, "xfalert-", new ArrayList<String>(), "", fakeStreamNames);

        Map<String, Map<String,Object>> gauges = parseGauges(f.asJson());

        assertEquals(5, gauges.size());

        assertEquals("xfalert-graylog2-server", gauges.get("xfalert-total").get("source"));
        assertEquals((long) 2, gauges.get("xfalert-total").get("value"));
        assertEquals((long) 3, gauges.get("xfalert-host-foo-example-org").get("value"));
        assertEquals((long) 1, gauges.get("xfalert-host-bar-example-org").get("value"));
        assertEquals((long) 2, gauges.get("xfalert-stream-lol-stream1").get("value"));
        assertEquals((long) 1, gauges.get("xfalert-stream-lolanotherstream").get("value"));*/
    }

    @Test
    public void testAsJsonWithEmptyCounter() throws IOException {
        /*MessageCounterImpl counter = new MessageCounterImpl();
        LibratoMetricsFormatter f = new LibratoMetricsFormatter(counter, "xfalert-", new ArrayList<String>(), "", new HashMap<String, String>());

        Map<String, Map<String,Object>> gauges = parseGauges(f.asJson());

        assertEquals(1, gauges.size());
        assertEquals((long) 0, gauges.get("xfalert-total").get("value"));*/
    }

    @Test
    public void testAsJsonWithConfiguredStreamFilter() throws IOException {
        /*MessageCounterImpl counter = new MessageCounterImpl();

        // Total: 2
        counter.incrementTotal();
        counter.incrementTotal();

        // Host foo.example.org: 3
        counter.incrementSource("foo.example.org");
        counter.incrementSource("foo.example.org");
        counter.incrementSource("foo.example.org");

        // Host bar.example.org: 1
        counter.incrementSource("bar.example.org");

        Map<String, String> fakeStreamNames = Maps.newHashMap();
        
        // Stream id1: 2
        ObjectId id1 = new ObjectId();
        fakeStreamNames.put(id1.toString(), "some_stream");
        counter.incrementStream(id1);
        counter.incrementStream(id1);

        // Stream id2: 1
        ObjectId id2 = new ObjectId();
        counter.incrementStream(id2);

        // Stream id3: 1
        ObjectId id3 = new ObjectId();
        counter.incrementStream(id3);

        List<String> streamFilter = new ArrayList<String>();
        streamFilter.add(id1.toString());
        streamFilter.add(id3.toString());
        streamFilter.add(new ObjectId().toString());

        LibratoMetricsFormatter f = new LibratoMetricsFormatter(counter, "xfalert-", streamFilter, "", fakeStreamNames);

        Map<String, Map<String,Object>> gauges = parseGauges(f.asJson());

        assertEquals(4, gauges.size());

        assertEquals("xfalert-graylog2-server", gauges.get("xfalert-total").get("source"));
        assertEquals((long) 2, gauges.get("xfalert-total").get("value"));
        assertEquals((long) 3, gauges.get("xfalert-host-foo-example-org").get("value"));
        assertEquals((long) 1, gauges.get("xfalert-host-bar-example-org").get("value"));
        assertEquals((long) 1, gauges.get("xfalert-stream-noname-" + id2.toString()).get("value"));*/
    }

    @Test
    public void testAsJsonWithConfiguredHostFilter() throws IOException {
        /*MessageCounterImpl counter = new MessageCounterImpl();

        // Total: 2
        counter.incrementTotal();
        counter.incrementTotal();

        // Host foo.example.org: 3
        counter.incrementSource("foo.example.org");
        counter.incrementSource("foo.example.org");
        counter.incrementSource("foo.example.org");

        // Host bar.example.org: 1
        counter.incrementSource("bar.example.org");

        // Host bar.lolwut.example.org: 1
        counter.incrementSource("bar.lolwut.example.org");

        Map<String, String> fakeStreamNames = Maps.newHashMap();
        
        // Stream id1: 2
        ObjectId id1 = new ObjectId();
        fakeStreamNames.put(id1.toString(), "some_stream");
        counter.incrementStream(id1);
        counter.incrementStream(id1);

        // Stream id2: 1
        ObjectId id2 = new ObjectId();
        fakeStreamNames.put(id2.toString(), " some_stream__ ___2 ");
        counter.incrementStream(id2);

        String hostFilter = "^bar.*\\.example.org$";

        LibratoMetricsFormatter f = new LibratoMetricsFormatter(counter, "xfalert-", new ArrayList<String>(), hostFilter, fakeStreamNames);

        Map<String, Map<String,Object>> gauges = parseGauges(f.asJson());

        assertEquals(4, gauges.size());

        assertEquals("xfalert-graylog2-server", gauges.get("xfalert-total").get("source"));
        assertEquals(2L, gauges.get("xfalert-total").get("value"));
        assertEquals(3L, gauges.get("xfalert-host-foo-example-org").get("value"));
        assertEquals(2L, gauges.get("xfalert-stream-somestream").get("value"));
        assertEquals(1L, gauges.get("xfalert-stream-somestream2").get("value"));*/
    }

    private Map<String, Map<String,Object>> parseGauges(String json) throws IOException {
        Map<String, Map<String,Object>> result = Maps.newHashMap();

        JsonNode userData = objectMapper.readTree(json);
        JsonNode gauges = userData.get("gauges");

        for (JsonNode node : gauges) {
            Map<String, Object> gauge = Maps.newHashMap();
            gauge.put("source", node.get("source").asText());
            gauge.put("name", node.get("name").asText());
            gauge.put("value", node.get("value").asLong());
            result.put(node.get("name").asText(), gauge);
        }

        return result;
    }
}
