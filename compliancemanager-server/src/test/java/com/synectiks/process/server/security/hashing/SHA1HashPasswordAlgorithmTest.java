/*
 * */
package com.synectiks.process.server.security.hashing;

import org.junit.Before;
import org.junit.Test;

import com.synectiks.process.server.security.hashing.SHA1HashPasswordAlgorithm;

import static org.assertj.core.api.Assertions.assertThat;

public class SHA1HashPasswordAlgorithmTest {
    private SHA1HashPasswordAlgorithm SHA1HashPasswordAlgorithm;

    @Before
    public void setUp() throws Exception {
        this.SHA1HashPasswordAlgorithm = new SHA1HashPasswordAlgorithm("passwordSecret");
    }

    @Test
    public void testSupports() throws Exception {
        assertThat(SHA1HashPasswordAlgorithm.supports("deadbeefaffedeadbeefdeadbeefaffedeadbeef")).isTrue();
        assertThat(SHA1HashPasswordAlgorithm.supports("{bcrypt}foobar")).isFalse();
        assertThat(SHA1HashPasswordAlgorithm.supports("{foobar}foobar")).isFalse();
    }

    @Test
    public void testHash() throws Exception {
        assertThat(SHA1HashPasswordAlgorithm.hash("foobar")).isEqualTo("baae906e6bbb37ca5033600fcb4824c98b0430fb");
    }

    @Test
    public void testMatches() throws Exception {
        assertThat(SHA1HashPasswordAlgorithm.matches("baae906e6bbb37ca5033600fcb4824c98b0430fb", "foobar")).isTrue();
    }
}