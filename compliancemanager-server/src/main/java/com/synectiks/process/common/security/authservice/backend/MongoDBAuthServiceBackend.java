/*
 * */
package com.synectiks.process.common.security.authservice.backend;

import com.synectiks.process.common.security.authservice.AuthServiceBackend;
import com.synectiks.process.common.security.authservice.AuthServiceBackendDTO;
import com.synectiks.process.common.security.authservice.AuthServiceCredentials;
import com.synectiks.process.common.security.authservice.AuthenticationDetails;
import com.synectiks.process.common.security.authservice.ProvisionerService;
import com.synectiks.process.common.security.authservice.UserDetails;
import com.synectiks.process.common.security.authservice.test.AuthServiceBackendTestResult;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.plugin.security.PasswordAlgorithm;
import com.synectiks.process.server.security.PasswordAlgorithmFactory;
import com.synectiks.process.server.security.encryption.EncryptedValue;
import com.synectiks.process.server.security.encryption.EncryptedValueService;
import com.synectiks.process.server.shared.users.UserService;
import com.unboundid.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;

public class MongoDBAuthServiceBackend implements AuthServiceBackend {
    public static final String NAME = "internal-mongodb";
    private static final Logger LOG = LoggerFactory.getLogger(MongoDBAuthServiceBackend.class);

    private final UserService userService;
    private final EncryptedValueService encryptedValueService;
    private final PasswordAlgorithmFactory passwordAlgorithmFactory;

    @Inject
    public MongoDBAuthServiceBackend(UserService userService,
                                     EncryptedValueService encryptedValueService,
                                     PasswordAlgorithmFactory passwordAlgorithmFactory) {
        this.userService = userService;
        this.encryptedValueService = encryptedValueService;
        this.passwordAlgorithmFactory = passwordAlgorithmFactory;
    }

    @Override
    public Optional<AuthenticationDetails> authenticateAndProvision(AuthServiceCredentials authCredentials,
                                                          ProvisionerService provisionerService) {
        final String username = authCredentials.username();

        LOG.debug("Trying to load user <{}> from database", username);
        final User user = userService.load(username);
        if (user == null) {
            LOG.debug("User <{}> not found in database", username);
            return Optional.empty();
        }
        if (user.isLocalAdmin()) {
            throw new IllegalStateException("Local admin user should have been handled earlier and not reach the authentication service authenticator");
        }
        if (!user.getAccountStatus().equals(User.AccountStatus.ENABLED)) {
            LOG.warn("Account for user <{}> is disabled.", user.getName());
            return Optional.empty();
        }
        if (user.isExternalUser()) {
            // We don't store passwords for users synced from an authentication service, so we can't handle them here.
            LOG.trace("Skipping mongodb-based password check for external user {}", authCredentials.username());
            return Optional.empty();
        }

        if (!authCredentials.isAuthenticated()) {
            if (!isValidPassword(user, authCredentials.password())) {
                LOG.warn("Failed to validate password for user <{}>", username);
                return Optional.empty();
            }
        }

        LOG.debug("Successfully validated password for user <{}>", username);

        final UserDetails userDetails = provisionerService.provision(provisionerService.newDetails(this)
                .databaseId(user.getId())
                .username(user.getName())
                .accountIsEnabled(user.getAccountStatus().equals(User.AccountStatus.ENABLED))
                .email(user.getEmail())
                .fullName(user.getFullName())
                // No need to set default roles because MongoDB users will not be provisioned by the provisioner
                .defaultRoles(Collections.emptySet())
                .base64AuthServiceUid(Base64.encode(user.getId()))
                .build());

        return Optional.of(AuthenticationDetails.builder().userDetails(userDetails).build());
    }

    private boolean isValidPassword(User user, EncryptedValue password) {
        final PasswordAlgorithm passwordAlgorithm = passwordAlgorithmFactory.forPassword(user.getHashedPassword());
        if (passwordAlgorithm == null) {
            return false;
        }
        return passwordAlgorithm.matches(user.getHashedPassword(), encryptedValueService.decrypt(password));
    }

    @Override
    public String backendType() {
        return NAME;
    }

    @Override
    public String backendId() {
        return AuthServiceBackend.INTERNAL_BACKEND_ID;
    }

    @Override
    public String backendTitle() {
        return "Internal MongoDB";
    }

    @Override
    public AuthServiceBackendDTO prepareConfigUpdate(AuthServiceBackendDTO existingBackend, AuthServiceBackendDTO newBackend) {
        return newBackend;
    }

    @Override
    public AuthServiceBackendTestResult testConnection(@Nullable AuthServiceBackendDTO existingBackendConfig) {
        return AuthServiceBackendTestResult.createFailure("Not implemented");
    }

    @Override
    public AuthServiceBackendTestResult testLogin(AuthServiceCredentials credentials, @Nullable AuthServiceBackendDTO existingConfig) {
        return AuthServiceBackendTestResult.createFailure("Not implemented");
    }
}
