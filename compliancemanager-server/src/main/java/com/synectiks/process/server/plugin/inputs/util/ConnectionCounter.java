/*
 * */
package com.synectiks.process.server.plugin.inputs.util;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@ChannelHandler.Sharable
public class ConnectionCounter extends ChannelInboundHandlerAdapter {
    private final AtomicInteger connections;
    private final AtomicLong totalConnections;

    public ConnectionCounter(AtomicInteger connections, AtomicLong totalConnections) {
        this.connections = connections;
        this.totalConnections = totalConnections;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        connections.incrementAndGet();
        totalConnections.incrementAndGet();
        ctx.channel().closeFuture().addListener(f -> connections.decrementAndGet());

        super.channelActive(ctx);
    }

    public int getConnectionCount() {
        return connections.get();
    }

    public long getTotalConnections() {
        return totalConnections.get();
    }
}
