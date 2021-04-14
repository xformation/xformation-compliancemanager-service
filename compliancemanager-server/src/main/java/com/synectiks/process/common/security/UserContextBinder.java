/*
 * */
package com.synectiks.process.common.security;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.process.internal.RequestScoped;

// TODO this only works for method injection.
// In theory, by using proxies, it should also work for constructors, etc.
// See: https://stackoverflow.com/a/38060472
public class UserContextBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bindFactory(UserContextFactory.class)
                .to(UserContext.class)
                .in(RequestScoped.class);
    }
}
