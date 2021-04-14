/*
 * */
package com.synectiks.process.server.shared.utilities;

import org.junit.Test;

import com.synectiks.process.server.shared.utilities.ByteBufferUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class ByteBufferUtilsTest {
    @Test
    public void readBytesFromArrayBackedByteBuffer() {
        final byte[] bytes = "FOOBAR".getBytes(StandardCharsets.US_ASCII);
        final ByteBuffer buffer1 = ByteBuffer.wrap(bytes);
        final ByteBuffer buffer2 = ByteBuffer.wrap(bytes);
        final byte[] readBytesComplete = ByteBufferUtils.readBytes(buffer1);
        final byte[] readBytesPartial = ByteBufferUtils.readBytes(buffer2, 0, 3);

        assertThat(readBytesComplete).isEqualTo(bytes);
        assertThat(readBytesPartial).isEqualTo(Arrays.copyOf(bytes, 3));
    }

    @Test
    public void readBytesFromNonArrayBackedByteBuffer() {
        final byte[] bytes = "FOOBAR".getBytes(StandardCharsets.US_ASCII);
        final ByteBuffer buffer1 = ByteBuffer.allocateDirect(1024);
        buffer1.put(bytes).flip();
        final ByteBuffer buffer2 = ByteBuffer.allocateDirect(1024);
        buffer2.put(bytes).flip();

        final byte[] readBytesComplete = ByteBufferUtils.readBytes(buffer1);
        final byte[] readBytesPartial = ByteBufferUtils.readBytes(buffer2, 0, 3);

        assertThat(readBytesComplete).isEqualTo(bytes);
        assertThat(readBytesPartial).isEqualTo(Arrays.copyOf(bytes, 3));
    }
}