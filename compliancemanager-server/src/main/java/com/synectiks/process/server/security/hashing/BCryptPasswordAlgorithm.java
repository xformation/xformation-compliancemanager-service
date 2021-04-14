/*
 * */
package com.synectiks.process.server.security.hashing;

import org.mindrot.jbcrypt.BCrypt;

import com.synectiks.process.server.plugin.security.PasswordAlgorithm;

import javax.inject.Inject;
import javax.inject.Named;

import static com.google.common.base.Preconditions.checkArgument;

public class BCryptPasswordAlgorithm implements PasswordAlgorithm {
    private static final String PREFIX = "{bcrypt}";
    private static final String SALT_PREFIX = "{salt}";

    private final Integer saltSize;

    @Inject
    public BCryptPasswordAlgorithm(@Named("user_password_bcrypt_salt_size") Integer saltSize) {
        this.saltSize = saltSize;
    }

    @Override
    public boolean supports(String hashedPassword) {
        return hashedPassword.startsWith(PREFIX) && hashedPassword.contains(SALT_PREFIX);
    }

    private String hash(String password, String salt) {
        return PREFIX + BCrypt.hashpw(password, salt) + SALT_PREFIX + salt;
    }

    @Override
    public String hash(String password) {
        return hash(password, BCrypt.gensalt(this.saltSize));
    }

    @Override
    public boolean matches(String hashedPasswordAndSalt, String otherPassword) {
        checkArgument(supports(hashedPasswordAndSalt), "Supplied hashed password is not supported, it does not start with "
                + PREFIX + " or does not contain a salt.");

        final int saltIndex = hashedPasswordAndSalt.lastIndexOf(SALT_PREFIX);
        final String salt = hashedPasswordAndSalt.substring(saltIndex + SALT_PREFIX.length());
        return hash(otherPassword, salt).equals(hashedPasswordAndSalt);
    }
}
