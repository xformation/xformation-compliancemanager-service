/*
 * */
package com.synectiks.process.common.security.authservice;

public interface ProvisionerAction {
    interface Factory<TYPE extends ProvisionerAction> {
        TYPE create(String authServiceId);
    }

    void provision(UserDetails userDetails) throws ProvisionException;
}
