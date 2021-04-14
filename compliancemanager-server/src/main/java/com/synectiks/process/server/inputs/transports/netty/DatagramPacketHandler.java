/*
 * */
package com.synectiks.process.server.inputs.transports.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

@ChannelHandler.Sharable
public class DatagramPacketHandler extends MessageToMessageDecoder<DatagramPacket> {
    public static final DatagramPacketHandler INSTANCE = new DatagramPacketHandler();

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        out.add(ReferenceCountUtil.retain(SenderEnvelope.of(msg.content(), msg.sender())));
    }
}
