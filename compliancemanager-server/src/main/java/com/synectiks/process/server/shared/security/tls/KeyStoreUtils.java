/*
 * */
package com.synectiks.process.server.shared.security.tls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public final class KeyStoreUtils {
    private KeyStoreUtils() {
    }

    public static byte[] getBytes(KeyStore keyStore, char[] password) throws GeneralSecurityException, IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        keyStore.store(stream, password);

        return stream.toByteArray();
    }
}
