/*
 * */
package com.synectiks.process.common.plugins.netflow.utils;

import com.google.common.net.InetAddresses;
import io.netty.buffer.ByteBuf;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ByteBufUtils {
    public static final InetAddress DEFAULT_INET_ADDRESS = InetAddresses.forString("0.0.0.0");

    public static long getUnsignedInteger(final ByteBuf buf, final int offset, final int length) {
        switch (length) {
            case 1:
                return buf.getUnsignedByte(offset);
            case 2:
                return buf.getUnsignedShort(offset);
            case 3:
                return buf.getUnsignedMedium(offset);
            case 4:
                return buf.getUnsignedInt(offset);
            case 8:
                return buf.getLong(offset) & 0x00000000ffffffffL;
            default:
                return 0L;
        }
    }

    public static InetAddress getInetAddress(final ByteBuf buf, final int offset, final int length) {
        final byte[] data = new byte[length];
        buf.getBytes(offset, data, 0, length);

        return getInetAddress(data);
    }

    public static InetAddress readInetAddress(final ByteBuf buf) {
        final byte[] data = new byte[4];
        buf.readBytes(data);

        return getInetAddress(data);
    }

    private static InetAddress getInetAddress(byte[] data) {
        InetAddress address;
        try {
            address = InetAddress.getByAddress(data);
        } catch (UnknownHostException e) {
            address = DEFAULT_INET_ADDRESS;
        }

        return address;
    }
}
