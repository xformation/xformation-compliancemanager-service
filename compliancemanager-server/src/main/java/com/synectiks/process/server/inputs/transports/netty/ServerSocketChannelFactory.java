/*
 * */
package com.synectiks.process.server.inputs.transports.netty;

import io.netty.channel.ChannelFactory;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerSocketChannelFactory implements ChannelFactory<ServerSocketChannel> {
    private final NettyTransportType transportType;

    public ServerSocketChannelFactory(NettyTransportType transportType) {
        this.transportType = transportType;
    }

    @Override
    public ServerSocketChannel newChannel() {
        switch (transportType) {
            case EPOLL:
                return new EpollServerSocketChannel();
            case KQUEUE:
                return new KQueueServerSocketChannel();
            case NIO:
                return new NioServerSocketChannel();
            default:
                throw new IllegalArgumentException("Invalid or unknown Netty transport type " + transportType);
        }
    }
}
