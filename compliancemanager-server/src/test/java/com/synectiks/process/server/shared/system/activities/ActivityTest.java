/*
 * */
package com.synectiks.process.server.shared.system.activities;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import com.synectiks.process.server.shared.system.activities.Activity;

public class ActivityTest {
    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(Activity.class)
            .suppress(Warning.NONFINAL_FIELDS)
            .verify();
    }
}
