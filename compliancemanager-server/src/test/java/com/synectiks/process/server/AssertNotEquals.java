/*
 * */
package com.synectiks.process.server;

import static org.junit.Assert.assertFalse;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public class AssertNotEquals {
    public static <T> void assertNotEquals(String msg, T obj1, T obj2) {
        if (obj1 == null)
            assertFalse(msg, obj1 == null && obj2 == null);
        else
            assertFalse(msg, obj1.equals(obj2));
    }
}
