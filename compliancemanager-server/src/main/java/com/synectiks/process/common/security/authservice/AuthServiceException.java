/*
 * */
package com.synectiks.process.common.security.authservice;

import java.util.Locale;

public class AuthServiceException extends RuntimeException {
    private final String backendType;
    private final String backendId;

    private static String toMessage(String message, String backendType, String backendId) {
        return String.format(Locale.US, "AuthenticationService[%s/%s]: %s", backendType, backendId, message);
    }

    public AuthServiceException(String message, String backendType, String backendId) {
        super(toMessage(message, backendType, backendId));
        this.backendType = backendType;
        this.backendId = backendId;
    }

    public AuthServiceException(String message, String backendType, String backendId, Throwable cause) {
        super(toMessage(message, backendType, backendId), cause);
        this.backendType = backendType;
        this.backendId = backendId;
    }

    public String getBackendType() {
        return backendType;
    }

    public String getBackendId() {
        return backendId;
    }
}
