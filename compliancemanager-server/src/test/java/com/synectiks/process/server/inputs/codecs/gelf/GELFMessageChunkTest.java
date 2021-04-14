/*
 * */
package com.synectiks.process.server.inputs.codecs.gelf;

import org.junit.Test;

import com.synectiks.process.server.inputs.TestHelper;
import com.synectiks.process.server.inputs.codecs.gelf.GELFMessageChunk;
import com.synectiks.process.server.plugin.Tools;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class GELFMessageChunkTest {
    public final static String GELF_JSON = "{\"message\":\"foo\",\"host\":\"bar\",\"_lol_utf8\":\"ü\"}";

    private GELFMessageChunk buildChunk() throws Exception {
        String id = "lolwat67";
        int seqNum = 3;
        int seqCnt = 4;
        byte[] data = TestHelper.gzipCompress(GELF_JSON);

        return new GELFMessageChunk(TestHelper.buildGELFMessageChunk(id, seqNum, seqCnt, data), null);
    }

    @Test
    public void testGetArrival() throws Exception {
        final GELFMessageChunk chunk = buildChunk();
        final long l = Tools.nowUTC().getMillis();
        final long arrival = chunk.getArrival();
        assertTrue(l - arrival < 5000L); // delta shmelta
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(TestHelper.toHex("lolwat67"), buildChunk().getId());
    }

    @Test
    public void testGetData() throws Exception {
        assertArrayEquals(TestHelper.gzipCompress(GELF_JSON), buildChunk().getData());
    }

    @Test
    public void testGetSequenceCount() throws Exception {
        assertEquals(4, buildChunk().getSequenceCount());
    }

    @Test
    public void testGetSequenceNumber() throws Exception {
        assertEquals(3, buildChunk().getSequenceNumber());
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(buildChunk().toString());
    }

}
