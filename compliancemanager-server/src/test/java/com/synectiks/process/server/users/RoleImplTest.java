/*
 * */
package com.synectiks.process.server.users;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import com.synectiks.process.server.users.RoleImpl;

public class RoleImplTest {

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(RoleImpl.class)
            .suppress(Warning.NONFINAL_FIELDS)
            .verify();
    }
}
