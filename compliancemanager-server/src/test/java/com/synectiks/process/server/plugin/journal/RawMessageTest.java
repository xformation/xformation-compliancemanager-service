/*
 * */
package com.synectiks.process.server.plugin.journal;

import org.junit.Test;

import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.journal.RawMessage;
import com.synectiks.process.server.plugin.system.NodeId;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RawMessageTest {
    @Test
    public void minimalEncodeDecode() throws IOException {
        final RawMessage rawMessage = new RawMessage("testmessage".getBytes(StandardCharsets.UTF_8));
        final File tempFile = File.createTempFile("node", "test");
        rawMessage.addSourceNode("inputid", new NodeId(tempFile.getAbsolutePath()));
        rawMessage.setCodecName("raw");
        rawMessage.setCodecConfig(Configuration.EMPTY_CONFIGURATION);

        final byte[] encoded = rawMessage.encode();
        final RawMessage decodedMsg = RawMessage.decode(encoded, 1);

        assertNotNull(decodedMsg);
        assertArrayEquals("testmessage".getBytes(StandardCharsets.UTF_8), decodedMsg.getPayload());
        assertEquals("raw", decodedMsg.getCodecName());
    }
}
