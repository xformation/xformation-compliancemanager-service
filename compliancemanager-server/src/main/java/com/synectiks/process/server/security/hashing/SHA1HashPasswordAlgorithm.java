/*
 * */
package com.synectiks.process.server.security.hashing;

import org.apache.shiro.crypto.hash.SimpleHash;

import com.synectiks.process.server.plugin.security.PasswordAlgorithm;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.regex.Pattern;

public class SHA1HashPasswordAlgorithm implements PasswordAlgorithm {
    private static final String HASH_ALGORITHM = "SHA-1";
    private static final Pattern prefixPattern = Pattern.compile("^[a-f0-9]{40}$");
    private final String passwordSecret;

    @Inject
    public SHA1HashPasswordAlgorithm(@Named("password_secret") String passwordSecret) {
        this.passwordSecret = passwordSecret;
    }

    @Override
    public boolean supports(String hashedPassword) {
        return prefixPattern.matcher(hashedPassword).matches();
    }

    private String hash(String password, String salt) {
        return new SimpleHash(HASH_ALGORITHM, password, salt).toString();
    }

    @Override
    public String hash(String password) {
        return hash(password, passwordSecret);
    }

    @Override
    public boolean matches(String hashedPassword, String otherPassword) {
        if (supports(hashedPassword)) {
            return hash(otherPassword).equals(hashedPassword);
        } else {
            throw new IllegalArgumentException("Supplied hashed password is not supported.");
        }
    }
}
