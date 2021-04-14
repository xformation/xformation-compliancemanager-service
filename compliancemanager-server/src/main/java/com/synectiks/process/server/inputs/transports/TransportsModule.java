/*
 * */
package com.synectiks.process.server.inputs.transports;

import com.google.inject.multibindings.MapBinder;
import com.synectiks.process.server.inputs.transports.netty.EventLoopGroupFactory;
import com.synectiks.process.server.inputs.transports.netty.EventLoopGroupProvider;
import com.synectiks.process.server.plugin.inject.Graylog2Module;
import com.synectiks.process.server.plugin.inputs.transports.Transport;

import io.netty.channel.EventLoopGroup;

public class TransportsModule extends Graylog2Module {
    @Override
    protected void configure() {
        final MapBinder<String, Transport.Factory<? extends Transport>> mapBinder = transportMapBinder();

        installTransport(mapBinder, "udp", UdpTransport.class);
        installTransport(mapBinder, "tcp", TcpTransport.class);
        installTransport(mapBinder, "http", HttpTransport.class);
        installTransport(mapBinder, "randomhttp", RandomMessageTransport.class);
        installTransport(mapBinder, "kafka", KafkaTransport.class);
        installTransport(mapBinder, "amqp", AmqpTransport.class);
        installTransport(mapBinder, "httppoll", HttpPollTransport.class);
        installTransport(mapBinder, "syslog-tcp", SyslogTcpTransport.class);

        bind(EventLoopGroupFactory.class).asEagerSingleton();
        bind(EventLoopGroup.class).toProvider(EventLoopGroupProvider.class).asEagerSingleton();
    }
}
