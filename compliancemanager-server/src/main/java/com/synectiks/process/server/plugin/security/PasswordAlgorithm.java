/*
 * */
package com.synectiks.process.server.plugin.security;

public interface PasswordAlgorithm {
    boolean supports(String hashedPassword);

    String hash(String password);

    boolean matches(String hashedPassword, String otherPassword);
}
