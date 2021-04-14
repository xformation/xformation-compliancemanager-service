/*
 * */
package com.synectiks.process.server.utilities;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.synectiks.process.server.utilities.IpSubnet;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class IpSubnetTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"127.0.0.1/32", "127.0.0.1", "127.0.0.1", "127.0.0.1", true},
                {"127.0.0.1/32", "127.0.0.2", "127.0.0.1", "127.0.0.1", false},
                {"::1/128", "::1", "0:0:0:0:0:0:0:1", "0:0:0:0:0:0:0:1", true},
                {"::1/128", "::2", "0:0:0:0:0:0:0:1", "0:0:0:0:0:0:0:1", false},
                {"::1/128", "127.0.0.1", "0:0:0:0:0:0:0:1", "0:0:0:0:0:0:0:1", false},
                {"127.0.0.1/32", "::1", "127.0.0.1", "127.0.0.1", false},
                {"10.0.0.0/8", "10.1.2.3", "10.0.0.0", "10.255.255.255", true},
                {"2001:DB8::/128", "2001:DB8::1:2:3:4:5", "2001:db8:0:0:0:0:0:0", "2001:db8:0:0:0:0:0:0", false},
        });
    }

    private final IpSubnet ipSubnet;
    private final String ipAddress;
    private final String networkAddress;
    private final String broadcastAddress;
    private final boolean isInSubnet;

    public IpSubnetTest(String cidr, String ipAddress, String networkAddress, String broadcastAddress, boolean isInSubnet) throws UnknownHostException {
        this.ipSubnet = new IpSubnet(cidr);
        this.ipAddress = ipAddress;
        this.networkAddress = networkAddress;
        this.broadcastAddress = broadcastAddress;
        this.isInSubnet = isInSubnet;
    }

    @Test
    public void getNetworkAddress() {
        assertThat(ipSubnet.getNetworkAddress()).isEqualTo(networkAddress);
    }

    @Test
    public void getBroadcastAddress() {
        assertThat(ipSubnet.getBroadcastAddress()).isEqualTo(broadcastAddress);
    }

    @Test
    public void contains() throws UnknownHostException {
        assertThat(ipSubnet.contains(ipAddress)).isEqualTo(isInSubnet);
    }
}