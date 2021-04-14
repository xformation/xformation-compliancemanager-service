package com.synectiks.process.server.gelfclient.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

/**
 * A Netty channel handler which compresses messages using a {@link DeflaterOutputStream}.
 */
public class GelfCompressionZlibEncoder extends MessageToMessageEncoder<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream();
             final DeflaterOutputStream stream = new DeflaterOutputStream(bos)) {

            stream.write(msg.array());
            stream.finish();

            out.add(Unpooled.wrappedBuffer(bos.toByteArray()));
        }
    }
}
