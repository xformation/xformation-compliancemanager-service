/*
 * */
package com.synectiks.process.common.plugins.beats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Ints;
import com.synectiks.process.common.plugins.beats.Beats2Codec;
import com.synectiks.process.common.plugins.beats.BeatsFrameDecoder;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.journal.RawMessage;
import com.synectiks.process.server.shared.bindings.providers.ObjectMapperProvider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import static com.google.common.base.MoreObjects.firstNonNull;

public class ConsolePrinter {
    public static void main(String[] args) throws Exception {
        String hostname = "127.0.0.1";
        int port = 5044;
        if (args.length >= 2) {
            hostname = args[0];
            port = firstNonNull(Ints.tryParse(args[1]), 5044);
        }
        if (args.length >= 1) {
            port = firstNonNull(Ints.tryParse(args[1]), 5044);
        }


        final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            final ServerBootstrap b = new ServerBootstrap()
                    .group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("logging", new LoggingHandler());
                            ch.pipeline().addLast("beats-frame-decoder", new BeatsFrameDecoder());
                            ch.pipeline().addLast("beats-codec", new BeatsCodecHandler());
                        }
                    });

            System.out.println("Starting listener on " + hostname + ":" + port);
            final ChannelFuture future = b.bind(hostname, port).sync();
            future.channel().closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public static class BeatsCodecHandler extends SimpleChannelInboundHandler<ByteBuf> {
        private final ObjectMapper objectMapper = new ObjectMapperProvider().get();
        private final Beats2Codec beatsCodec = new Beats2Codec(Configuration.EMPTY_CONFIGURATION, objectMapper);

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf message) throws Exception {
            final int readableBytes = message.readableBytes();
            final byte[] messageBytes = new byte[readableBytes];
            message.readBytes(messageBytes);
            final RawMessage rawMessage = new RawMessage(messageBytes);

            final Message decodedMessage = beatsCodec.decode(rawMessage);
            System.out.println(decodedMessage);

            ctx.fireChannelRead(decodedMessage);
        }
    }
}
