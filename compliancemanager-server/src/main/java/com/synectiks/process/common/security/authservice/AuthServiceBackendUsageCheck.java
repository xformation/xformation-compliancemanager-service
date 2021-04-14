/*
 * */
package com.synectiks.process.common.security.authservice;

import javax.inject.Inject;

import com.synectiks.process.server.shared.users.UserService;

import java.util.Optional;

public class AuthServiceBackendUsageCheck {
    private final GlobalAuthServiceConfig globalAuthServiceConfig;
    private final UserService userService;

    @Inject
    public AuthServiceBackendUsageCheck(GlobalAuthServiceConfig globalAuthServiceConfig, UserService userService) {
        this.globalAuthServiceConfig = globalAuthServiceConfig;
        this.userService = userService;
    }

    public boolean isAuthServiceInUse(String authServiceBackendId) {
        // Check if the service is actively used
        final Optional<AuthServiceBackend> activeBackend = globalAuthServiceConfig.getActiveBackend();
        if (activeBackend.isPresent() && activeBackend.get().backendId().equals(authServiceBackendId)) {
            return true;
        }

        // Check if any users reference the service
        return userService.loadAllForAuthServiceBackend(authServiceBackendId).size() > 0;
    }
}
