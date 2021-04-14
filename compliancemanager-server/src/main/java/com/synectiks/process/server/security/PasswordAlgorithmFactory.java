/*
 * */
package com.synectiks.process.server.security;

import com.synectiks.process.server.plugin.security.PasswordAlgorithm;
import com.synectiks.process.server.users.DefaultPasswordAlgorithm;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Map;

public class PasswordAlgorithmFactory {
    private final Map<String, PasswordAlgorithm> passwordAlgorithms;
    private final PasswordAlgorithm defaultPasswordAlgorithm;

    @Inject
    public PasswordAlgorithmFactory(Map<String, PasswordAlgorithm> passwordAlgorithms,
                                    @DefaultPasswordAlgorithm PasswordAlgorithm defaultPasswordAlgorithm) {
        this.passwordAlgorithms = passwordAlgorithms;
        this.defaultPasswordAlgorithm = defaultPasswordAlgorithm;
    }

    @Nullable
    public PasswordAlgorithm forPassword(String hashedPassword) {
        for (PasswordAlgorithm passwordAlgorithm : passwordAlgorithms.values()) {
            if (passwordAlgorithm.supports(hashedPassword))
                return passwordAlgorithm;
        }

        return null;
    }

    @Nullable
    public PasswordAlgorithm forName(String name) {
        return this.passwordAlgorithms.get(name);
    }

    public PasswordAlgorithm defaultPasswordAlgorithm() {
        return defaultPasswordAlgorithm;
    }
}
