/*
 * */
package com.synectiks.process.server.security.encryption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.synectiks.process.server.security.encryption.EncryptedValue;
import com.synectiks.process.server.security.encryption.EncryptedValueService;

import static org.assertj.core.api.Assertions.assertThat;

class EncryptedValueServiceTest {
    private EncryptedValueService service;

    @BeforeEach
    void setUp() {
        this.service = new EncryptedValueService("1234567890abcdef");
    }

    @Test
    void encryptAndDecryption() {
        final EncryptedValue encryptedValue = service.encrypt("s3cr3t");

        assertThat(encryptedValue.value()).isNotBlank().isNotEqualTo("s3cr3t");
        assertThat(encryptedValue.salt()).isNotBlank();

        assertThat(service.decrypt(encryptedValue)).isEqualTo("s3cr3t");
    }
}
