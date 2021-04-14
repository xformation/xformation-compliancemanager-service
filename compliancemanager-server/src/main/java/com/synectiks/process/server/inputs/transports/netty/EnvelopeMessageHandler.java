/*
 * */
package com.synectiks.process.server.inputs.transports.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.plugin.inputs.MessageInput;
import com.synectiks.process.server.plugin.inputs.transports.NettyTransport;
import com.synectiks.process.server.plugin.journal.RawMessage;

import java.net.InetSocketAddress;

public class EnvelopeMessageHandler extends SimpleChannelInboundHandler<AddressedEnvelope<ByteBuf, InetSocketAddress>> {
    private static final Logger LOG = LoggerFactory.getLogger(NettyTransport.class);

    private final MessageInput input;

    public EnvelopeMessageHandler(MessageInput input) {
        this.input = input;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AddressedEnvelope<ByteBuf, InetSocketAddress> envelope) throws Exception {
        final ByteBuf msg = envelope.content();
        final byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        final RawMessage raw = new RawMessage(bytes, envelope.sender());
        input.processRawMessage(raw);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.debug("Could not handle message, closing connection: {}", cause);
        ctx.channel().close();
        super.exceptionCaught(ctx, cause);
    }
}
