/*
 * */
package com.synectiks.process.common.plugins.netflow;

import com.synectiks.process.common.plugins.netflow.codecs.NetFlowCodec;
import com.synectiks.process.common.plugins.netflow.inputs.NetFlowUdpInput;
import com.synectiks.process.common.plugins.netflow.transport.NetFlowUdpTransport;
import com.synectiks.process.server.plugin.PluginConfigBean;
import com.synectiks.process.server.plugin.PluginModule;

import java.util.Collections;
import java.util.Set;

public class NetFlowPluginModule extends PluginModule {
    @Override
    public Set<? extends PluginConfigBean> getConfigBeans() {
        return Collections.emptySet();
    }

    @Override
    protected void configure() {
        addMessageInput(NetFlowUdpInput.class);
        addCodec("netflow", NetFlowCodec.class);
        addTransport("netflow-udp", NetFlowUdpTransport.class);
    }
}
