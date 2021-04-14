/*
 * */
package com.synectiks.process.common.security.authservice.test;

import javax.inject.Inject;

import com.synectiks.process.common.security.authservice.AuthServiceBackend;
import com.synectiks.process.common.security.authservice.AuthServiceBackendDTO;
import com.synectiks.process.common.security.authservice.AuthServiceCredentials;
import com.synectiks.process.common.security.authservice.DBAuthServiceBackendService;

import java.util.Map;
import java.util.Optional;

public class AuthServiceBackendTestService {
    private final DBAuthServiceBackendService dbService;
    private final Map<String, AuthServiceBackend.Factory<? extends AuthServiceBackend>> backendFactories;

    @Inject
    public AuthServiceBackendTestService(DBAuthServiceBackendService dbService,
                                         Map<String, AuthServiceBackend.Factory<? extends AuthServiceBackend>> backendFactories) {
        this.dbService = dbService;
        this.backendFactories = backendFactories;
    }

    public AuthServiceBackendTestResult testConnection(AuthServiceBackendTestRequest request) {
        final Optional<AuthServiceBackend> backend = createNewBackend(request);

        if (backend.isPresent()) {
            return backend.get().testConnection(getExistingBackendConfig(request).orElse(null));
        }

        return AuthServiceBackendTestResult.createFailure("Unknown authentication service type: " + request.backendConfiguration().config().type());
    }

    public AuthServiceBackendTestResult testLogin(AuthServiceBackendTestRequest request) {
        final Optional<AuthServiceBackend> newBackend = createNewBackend(request);

        if (!request.userLogin().isPresent()) {
            return AuthServiceBackendTestResult.createFailure("Missing username and password");
        }

        if (newBackend.isPresent()) {
            return newBackend.get().testLogin(
                    AuthServiceCredentials.create(request.userLogin().get().username(), request.userLogin().get().password()),
                    getExistingBackendConfig(request).orElse(null)
            );
        }

        return AuthServiceBackendTestResult.createFailure("Unknown authentication service type: " + request.backendConfiguration().config().type());
    }

    private Optional<AuthServiceBackendDTO> getExistingBackendConfig(AuthServiceBackendTestRequest request) {
        if (request.backendId().isPresent()) {
            return dbService.get(request.backendId().get());
        }
        return Optional.empty();
    }

    private Optional<AuthServiceBackend> createNewBackend(AuthServiceBackendTestRequest request) {
        final AuthServiceBackendDTO newBackend = request.backendConfiguration();

        final AuthServiceBackend.Factory<? extends AuthServiceBackend> backendFactory = backendFactories.get(newBackend.config().type());
        if (backendFactory == null) {
            return Optional.empty();
        }

        return Optional.of(backendFactory.create(newBackend));
    }
}
