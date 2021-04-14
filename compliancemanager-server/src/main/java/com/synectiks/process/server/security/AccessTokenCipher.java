/*
 * */
package com.synectiks.process.server.security;

import com.google.common.hash.Hashing;
import com.synectiks.process.server.Configuration;

import javax.inject.Inject;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AccessTokenCipher {

    private final byte[] encryptionKey;

    @Inject
    public AccessTokenCipher(Configuration configuration) {
        // The password secret is only required to be at least 16 bytes long. Since the encryptSiv/decryptSiv methods
        // in AESTools require an encryption key that is at least 32 bytes long, we create a hash of the value.
        encryptionKey = Hashing.sha256().hashString(configuration.getPasswordSecret(), UTF_8).asBytes();
    }

    public String encrypt(String cleartext) {
        return AESTools.encryptSiv(cleartext, encryptionKey);
    }

    public String decrypt(String ciphertext) {
        return AESTools.decryptSiv(ciphertext, encryptionKey);
    }
}
