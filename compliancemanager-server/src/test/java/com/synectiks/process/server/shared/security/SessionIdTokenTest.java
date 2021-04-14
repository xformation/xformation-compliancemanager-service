/*
 * */
package com.synectiks.process.server.shared.security;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import com.synectiks.process.server.shared.security.SessionIdToken;

public class SessionIdTokenTest {

    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(SessionIdToken.class).verify();
    }
}
