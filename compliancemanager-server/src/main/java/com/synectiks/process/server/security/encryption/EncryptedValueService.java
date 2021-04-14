/*
 * */
package com.synectiks.process.server.security.encryption;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.synectiks.process.server.security.AESTools;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

@Singleton
public class EncryptedValueService {
    private final String encryptionKey;

    @Inject
    public EncryptedValueService(@Named("password_secret") String passwordSecret) {
        final String trimmedPasswordSecret = passwordSecret.trim();
        checkArgument(!isNullOrEmpty(trimmedPasswordSecret), "password secret cannot be null or empty");
        checkArgument(trimmedPasswordSecret.length() >= 16, "password secret must be at least 16 characters long");

        this.encryptionKey = trimmedPasswordSecret;
    }

    public EncryptedValue encrypt(String plainValue) {
        final String salt = AESTools.generateNewSalt();
        return EncryptedValue.builder()
                .value(AESTools.encrypt(plainValue, encryptionKey, salt))
                .salt(salt)
                .isKeepValue(false)
                .isDeleteValue(false)
                .build();
    }

    public String decrypt(EncryptedValue encryptedValue) {
        return AESTools.decrypt(encryptedValue.value(), encryptionKey, encryptedValue.salt());
    }
}
