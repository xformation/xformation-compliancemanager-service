/*
 * */
package com.synectiks.process.server.security;

import org.apache.shiro.realm.Realm;

import java.util.Collection;
import java.util.Optional;

/**
 * The generic type is Realm, even though it really only contains AuthenticatingRealms. This is simply to avoid having to
 * cast the generic collection when passing it to the SecurityManager.
 */
public interface OrderedAuthenticatingRealms extends Collection<Realm> {
    Optional<Realm> getRootAccountRealm();
}
