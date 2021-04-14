/*
 * */
package com.synectiks.process.server.bindings.providers;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.subject.Subject;

import com.synectiks.process.server.security.InMemoryRolePermissionResolver;
import com.synectiks.process.server.security.MongoDbSessionDAO;
import com.synectiks.process.server.security.OrderedAuthenticatingRealms;
import com.synectiks.process.server.shared.security.ThrowingFirstSuccessfulStrategy;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Singleton
public class DefaultSecurityManagerProvider implements Provider<DefaultSecurityManager> {
    private DefaultSecurityManager sm = null;

    @Inject
    public DefaultSecurityManagerProvider(MongoDbSessionDAO mongoDbSessionDAO,
                                          Map<String, AuthorizingRealm> authorizingOnlyRealms,
                                          InMemoryRolePermissionResolver inMemoryRolePermissionResolver,
                                          OrderedAuthenticatingRealms orderedAuthenticatingRealms) {
        sm = new DefaultSecurityManager(orderedAuthenticatingRealms);
        final Authenticator authenticator = sm.getAuthenticator();
        if (authenticator instanceof ModularRealmAuthenticator) {
            FirstSuccessfulStrategy strategy = new ThrowingFirstSuccessfulStrategy();
            strategy.setStopAfterFirstSuccess(true);
            ((ModularRealmAuthenticator) authenticator).setAuthenticationStrategy(strategy);
        }

        List<Realm> authorizingRealms = new ArrayList<>();
        authorizingRealms.addAll(authorizingOnlyRealms.values());
        // root account realm might be deactivated and won't be present in that case
        orderedAuthenticatingRealms.getRootAccountRealm().map(authorizingRealms::add);

        final ModularRealmAuthorizer authorizer = new ModularRealmAuthorizer(authorizingRealms);

        authorizer.setRolePermissionResolver(inMemoryRolePermissionResolver);
        sm.setAuthorizer(authorizer);

        final DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        final DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator() {
            @Override
            public boolean isSessionStorageEnabled(Subject subject) {
                // save to session if we already have a session. do not create on just for saving the subject
                return subject.getSession(false) != null;
            }
        };
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
        sm.setSubjectDAO(subjectDAO);

        final DefaultSessionManager defaultSessionManager = (DefaultSessionManager) sm.getSessionManager();
        defaultSessionManager.setSessionDAO(mongoDbSessionDAO);
        defaultSessionManager.setDeleteInvalidSessions(true);
        defaultSessionManager.setSessionValidationInterval(TimeUnit.MINUTES.toMillis(5));
        defaultSessionManager.setCacheManager(new MemoryConstrainedCacheManager());
        // DO NOT USE global session timeout!!! It's fucky.
        //defaultSessionManager.setGlobalSessionTimeout(TimeUnit.SECONDS.toMillis(5));

        SecurityUtils.setSecurityManager(sm);
    }

    @Override
    public DefaultSecurityManager get() {
        return sm;
    }
}
