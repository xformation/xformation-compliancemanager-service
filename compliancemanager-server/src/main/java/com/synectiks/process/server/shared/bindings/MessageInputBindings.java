/*
 * */
package com.synectiks.process.server.shared.bindings;

import com.google.inject.multibindings.MapBinder;
import com.synectiks.process.common.plugins.beats.BeatsInputPluginModule;
import com.synectiks.process.server.inputs.codecs.CodecsModule;
import com.synectiks.process.server.inputs.gelf.amqp.GELFAMQPInput;
import com.synectiks.process.server.inputs.gelf.http.GELFHttpInput;
import com.synectiks.process.server.inputs.gelf.kafka.GELFKafkaInput;
import com.synectiks.process.server.inputs.gelf.tcp.GELFTCPInput;
import com.synectiks.process.server.inputs.gelf.udp.GELFUDPInput;
import com.synectiks.process.server.inputs.misc.jsonpath.JsonPathInput;
import com.synectiks.process.server.inputs.random.FakeHttpMessageInput;
import com.synectiks.process.server.inputs.raw.amqp.RawAMQPInput;
import com.synectiks.process.server.inputs.raw.kafka.RawKafkaInput;
import com.synectiks.process.server.inputs.raw.tcp.RawTCPInput;
import com.synectiks.process.server.inputs.raw.udp.RawUDPInput;
import com.synectiks.process.server.inputs.syslog.amqp.SyslogAMQPInput;
import com.synectiks.process.server.inputs.syslog.kafka.SyslogKafkaInput;
import com.synectiks.process.server.inputs.syslog.tcp.SyslogTCPInput;
import com.synectiks.process.server.inputs.syslog.udp.SyslogUDPInput;
import com.synectiks.process.server.inputs.transports.TransportsModule;
import com.synectiks.process.server.plugin.inject.Graylog2Module;
import com.synectiks.process.server.plugin.inputs.MessageInput;

public class MessageInputBindings extends Graylog2Module {
    @Override
    protected void configure() {
        install(new TransportsModule());
        install(new CodecsModule());

        final MapBinder<String, MessageInput.Factory<? extends MessageInput>> inputMapBinder = inputsMapBinder();
        // new style inputs, using transports and codecs
        installInput(inputMapBinder, RawTCPInput.class, RawTCPInput.Factory.class);
        installInput(inputMapBinder, RawUDPInput.class, RawUDPInput.Factory.class);
        installInput(inputMapBinder, RawAMQPInput.class, RawAMQPInput.Factory.class);
        installInput(inputMapBinder, RawKafkaInput.class, RawKafkaInput.Factory.class);
        installInput(inputMapBinder, SyslogTCPInput.class, SyslogTCPInput.Factory.class);
        installInput(inputMapBinder, SyslogUDPInput.class, SyslogUDPInput.Factory.class);
        installInput(inputMapBinder, SyslogAMQPInput.class, SyslogAMQPInput.Factory.class);
        installInput(inputMapBinder, SyslogKafkaInput.class, SyslogKafkaInput.Factory.class);
        installInput(inputMapBinder, FakeHttpMessageInput.class, FakeHttpMessageInput.Factory.class);
        installInput(inputMapBinder, GELFTCPInput.class, GELFTCPInput.Factory.class);
        installInput(inputMapBinder, GELFHttpInput.class, GELFHttpInput.Factory.class);
        installInput(inputMapBinder, GELFUDPInput.class, GELFUDPInput.Factory.class);
        installInput(inputMapBinder, GELFAMQPInput.class, GELFAMQPInput.Factory.class);
        installInput(inputMapBinder, GELFKafkaInput.class, GELFKafkaInput.Factory.class);
        installInput(inputMapBinder, JsonPathInput.class, JsonPathInput.Factory.class);

        install(new BeatsInputPluginModule());
    }
}
