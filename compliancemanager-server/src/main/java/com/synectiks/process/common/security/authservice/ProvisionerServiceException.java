/*
 * */
package com.synectiks.process.common.security.authservice;

public class ProvisionerServiceException extends AuthServiceException {
    public ProvisionerServiceException(UserDetails userDetails) {
        super("Couldn't provision user: " + userDetails.username(), userDetails.authServiceType(), userDetails.authServiceId());
    }

    public ProvisionerServiceException(UserDetails userDetails, Throwable cause) {
        super("Couldn't provision user: " + userDetails.username(), userDetails.authServiceType(), userDetails.authServiceId(), cause);
    }
}
