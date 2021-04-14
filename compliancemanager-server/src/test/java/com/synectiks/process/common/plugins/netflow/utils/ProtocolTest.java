/*
 * */
package com.synectiks.process.common.plugins.netflow.utils;

import org.junit.Test;

import com.synectiks.process.common.plugins.netflow.utils.Protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProtocolTest {
    @Test
    public void test() throws Exception {
        final Protocol tcp = Protocol.TCP;

        assertEquals("tcp", tcp.getName());
        assertEquals(6, tcp.getNumber());
        assertEquals("TCP", tcp.getAlias());
    }

    @Test
    public void testGetByNumber() throws Exception {
        assertEquals(Protocol.TCP, Protocol.getByNumber(6));
        assertEquals(Protocol.VRRP, Protocol.getByNumber(112));
    }

    @Test
    public void testNull() throws Exception {
        assertNull(Protocol.getByNumber(1231323424));
    }
}
