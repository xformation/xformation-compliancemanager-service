/*
 * */
package com.synectiks.process.server;

import org.junit.Test;

import com.synectiks.process.server.HostSystem;

import static org.junit.Assert.assertTrue;

/**
 * @author lennart
 */
public class HostSystemTest {

    @Test
    public void testGetAvailableProcessors() {
        assertTrue(HostSystem.getAvailableProcessors() > 0);
    }

}
