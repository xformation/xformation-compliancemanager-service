/*
 * */
package com.synectiks.process.common.security.authservice;

public class ProvisionException extends Exception {
    public ProvisionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProvisionException(String message) {
        super(message);
    }
}
