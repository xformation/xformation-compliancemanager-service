/*
 * */
package com.synectiks.process.common.security.authservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.common.security.authservice.test.AuthServiceBackendTestResult;

import javax.annotation.Nullable;
import java.util.Optional;

public interface AuthServiceBackend {
    Logger log = LoggerFactory.getLogger(AuthServiceBackend.class);

    String INTERNAL_BACKEND_ID = "000000000000000000000001";

    interface Factory<TYPE extends AuthServiceBackend> {
        TYPE create(AuthServiceBackendDTO backend);
    }

    default Optional<AuthenticationDetails> authenticateAndProvision(AuthServiceCredentials authCredentials,
            ProvisionerService provisionerService) {
        log.debug("Cannot authenticate by username/password. Username/password authentication is not supported by " +
                "auth service backend type <" + backendType() + ">.");
        return Optional.empty();
    }

    default Optional<AuthenticationDetails> authenticateAndProvision(AuthServiceToken token,
            ProvisionerService provisionerService) {
        log.debug("Cannot authenticate by token. Token-based authentication is not supported by auth service backend " +
                "type <" + backendTitle() + ">.");
        return Optional.empty();
    }

    String backendType();

    String backendId();

    String backendTitle();

    AuthServiceBackendDTO prepareConfigUpdate(AuthServiceBackendDTO existingBackend, AuthServiceBackendDTO newBackend);

    AuthServiceBackendTestResult testConnection(@Nullable AuthServiceBackendDTO existingConfig);

    AuthServiceBackendTestResult testLogin(AuthServiceCredentials credentials, @Nullable AuthServiceBackendDTO existingConfig);
}
