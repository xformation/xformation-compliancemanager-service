/*
 * */
package com.synectiks.process.server.bindings.providers;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import com.synectiks.process.server.plugin.security.PasswordAlgorithm;

import java.util.Map;

public class DefaultPasswordAlgorithmProvider implements Provider<PasswordAlgorithm> {
    private final PasswordAlgorithm defaultPasswordAlgorithm;

    @Inject
    public DefaultPasswordAlgorithmProvider(@Named("user_password_default_algorithm") String defaultPasswordAlgorithmName,
                                            Map<String, PasswordAlgorithm> passwordAlgorithms) {
        if (passwordAlgorithms.containsKey(defaultPasswordAlgorithmName)) {
            this.defaultPasswordAlgorithm = passwordAlgorithms.get(defaultPasswordAlgorithmName);
        } else {
            throw new IllegalArgumentException("Invalid default password hashing specified in config. Found: "
                    + defaultPasswordAlgorithmName + ". Valid options: " + passwordAlgorithms.keySet());
        }
    }

    @Override
    public PasswordAlgorithm get() {
        return defaultPasswordAlgorithm;
    }
}
