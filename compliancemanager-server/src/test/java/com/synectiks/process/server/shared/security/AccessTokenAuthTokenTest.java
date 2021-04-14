/*
 * */
package com.synectiks.process.server.shared.security;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import com.synectiks.process.server.shared.security.AccessTokenAuthToken;

public class AccessTokenAuthTokenTest {
    @Test
    public void testEquals() throws Exception {
        EqualsVerifier.forClass(AccessTokenAuthToken.class)
            .verify();
    }
}
