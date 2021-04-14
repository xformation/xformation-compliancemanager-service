/*
 * */
package com.synectiks.process.server.inputs.transports.netty;

import io.netty.channel.ChannelFactory;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.kqueue.KQueueDatagramChannel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class DatagramChannelFactory implements ChannelFactory<DatagramChannel> {
    private final NettyTransportType transportType;

    public DatagramChannelFactory(NettyTransportType transportType) {
        this.transportType = transportType;
    }

    @Override
    public DatagramChannel newChannel() {
        switch (transportType) {
            case EPOLL:
                return new EpollDatagramChannel();
            case KQUEUE:
                return new KQueueDatagramChannel();
            case NIO:
                return new NioDatagramChannel();
            default:
                throw new IllegalArgumentException("Invalid or unknown Netty transport type " + transportType);
        }
    }
}
