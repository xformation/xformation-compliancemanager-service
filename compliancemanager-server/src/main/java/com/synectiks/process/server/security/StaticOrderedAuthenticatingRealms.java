/*
 * */
package com.synectiks.process.server.security;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.server.security.realm.AccessTokenAuthenticator;
import com.synectiks.process.server.security.realm.BearerTokenRealm;
import com.synectiks.process.server.security.realm.HTTPHeaderAuthenticationRealm;
import com.synectiks.process.server.security.realm.RootAccountRealm;
import com.synectiks.process.server.security.realm.SessionAuthenticator;
import com.synectiks.process.server.security.realm.UsernamePasswordRealm;

import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.Realm;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Statically ordered collection of Shiro AuthenticatingRealms.
 */
@Singleton
public class StaticOrderedAuthenticatingRealms extends AbstractCollection<Realm> implements OrderedAuthenticatingRealms {
    private static final ImmutableList<String> REALM_ORDER = ImmutableList.of(
            SessionAuthenticator.NAME,
            AccessTokenAuthenticator.NAME,
            HTTPHeaderAuthenticationRealm.NAME,
            UsernamePasswordRealm.NAME,
            BearerTokenRealm.NAME,
            RootAccountRealm.NAME // Should come last because it's (hopefully) not used that often
    );

    private final List<Realm> orderedRealms;

    @Inject
    public StaticOrderedAuthenticatingRealms(Map<String, AuthenticatingRealm> realms) {
        this.orderedRealms = REALM_ORDER.stream()
                .map(realms::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (orderedRealms.size() < 1) {
            throw new IllegalStateException("No realms available, this must not happen!");
        }
    }

    @Nonnull
    @Override
    public Iterator<Realm> iterator() {
        return orderedRealms.iterator();
    }

    @Override
    public int size() {
        return orderedRealms.size();
    }

    @Override
    public Optional<Realm> getRootAccountRealm() {
        return orderedRealms.stream().filter(r -> r instanceof RootAccountRealm).findFirst();
    }
}
