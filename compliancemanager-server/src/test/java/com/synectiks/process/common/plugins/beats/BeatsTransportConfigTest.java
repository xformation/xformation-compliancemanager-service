/*
 * */
package com.synectiks.process.common.plugins.beats;

import org.junit.Test;

import com.synectiks.process.common.plugins.beats.BeatsTransport;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.inputs.transports.NettyTransport;

import static org.assertj.core.api.Assertions.assertThat;

public class BeatsTransportConfigTest {
    @Test
    public void getRequestedConfigurationOverridesDefaultPort() throws Exception {
        final BeatsTransport.Config config = new BeatsTransport.Config();
        final ConfigurationRequest requestedConfiguration = config.getRequestedConfiguration();

        assertThat(requestedConfiguration.containsField(NettyTransport.CK_PORT)).isTrue();
        assertThat(requestedConfiguration.getField(NettyTransport.CK_PORT).getDefaultValue()).isEqualTo(5044);
    }
}