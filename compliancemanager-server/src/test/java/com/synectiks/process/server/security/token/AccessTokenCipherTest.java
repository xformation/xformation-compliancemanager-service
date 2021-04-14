/*
 * */
package com.synectiks.process.server.security.token;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.security.AccessTokenCipher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class AccessTokenCipherTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private AccessTokenCipher cipher;

    @Mock
    Configuration configuration;

    @Before
    public void setUp() throws Exception {
        when(configuration.getPasswordSecret()).thenReturn("0123456789ABCDEF");

        cipher = new AccessTokenCipher(configuration);
    }

    @Test
    public void roundTrip() {
        assertThat(cipher.decrypt(cipher.encrypt("cleartext"))).isEqualTo("cleartext");
    }

    @Test
    public void encrypt() {
        final String ciphertext = cipher.encrypt("cleartext");
        assertThat(ciphertext).isEqualTo("e5cb18c577b661b60ef0810675962f3fb9b0c2ad63b9e76afb");
    }

    @Test
    public void decrypt() {
        final String cleartext = cipher.decrypt("e5cb18c577b661b60ef0810675962f3fb9b0c2ad63b9e76afb");
        assertThat(cleartext).isEqualTo("cleartext");
    }

    @Test
    public void encryptionIsDeterministic() {
        final String c1 = cipher.encrypt("cleartext");
        final String c2 = cipher.encrypt("cleartext");
        final String c3 = cipher.encrypt("cleartext");
        assertThat(c1).isEqualTo(c2)
                .isEqualTo(c3);
    }
}
