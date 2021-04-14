/*
 * */
package com.synectiks.process.server.shared.security;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.audit.AuditActor;
import com.synectiks.process.server.audit.AuditEventSender;
import com.synectiks.process.server.plugin.database.users.User;
import com.synectiks.process.server.shared.users.UserService;
import com.synectiks.process.server.users.UserImpl;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;

import static com.synectiks.process.server.audit.AuditEventTypes.SESSION_CREATE;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SessionCreator {
    private static final Logger log = LoggerFactory.getLogger(SessionCreator.class);

    private final UserService userService;
    private final AuditEventSender auditEventSender;

    @Inject
    public SessionCreator(UserService userService, AuditEventSender auditEventSender) {
        this.userService = userService;
        this.auditEventSender = auditEventSender;
    }

    /**
     * Attempts to log the user in with the given authentication token and returns a new or renewed session upon
     * success.
     * <p>
     * Side effect: the user will be registered with the current security context.
     *
     * @param currentSessionId A session id, if one exists currently.
     * @param host Host the request to create a session originates from.
     * @param authToken Authentication token to log the user in.
     * @return A session for the authenticated user wrapped in an {@link Optional}, or an empty {@link Optional} if
     *         authentication failed.
     * @throws AuthenticationServiceUnavailableException If authenticating the user fails not due to an issue with the
     *                                                   credentials but because of an external resource being
     *                                                   unavailable
     */
    public Optional<Session> create(@Nullable String currentSessionId, String host,
            ActorAwareAuthenticationToken authToken) throws AuthenticationServiceUnavailableException {

        final String previousSessionId = StringUtils.defaultIfBlank(currentSessionId, null);
        final Subject subject = new Subject.Builder().sessionId(previousSessionId).host(host).buildSubject();

        ThreadContext.bind(subject);

        try {
            final Session session = subject.getSession();

            subject.login(authToken);

            String userId = subject.getPrincipal().toString();
            final User user = userService.loadById(userId);

            if (user != null) {
                long timeoutInMillis = user.getSessionTimeoutMs();
                session.setTimeout(timeoutInMillis);
                session.setAttribute("username", user.getName());
                getSessionAttributes(subject).forEach(session::setAttribute);
            } else {
                // set a sane default. really we should be able to load the user from above.
                session.setTimeout(UserImpl.DEFAULT_SESSION_TIMEOUT_MS);
            }
            session.touch();

            // save subject in session, otherwise we can't get the username back in subsequent requests.
            ((DefaultSecurityManager) SecurityUtils.getSecurityManager()).getSubjectDAO().save(subject);

            final Map<String, Object> auditEventContext = ImmutableMap.of(
                    "session_id", session.getId(),
                    "remote_address", host
            );
            auditEventSender.success(AuditActor.user(user.getName()), SESSION_CREATE, auditEventContext);

            return Optional.of(session);
        } catch (AuthenticationServiceUnavailableException e) {
            log.info("Session creation failed due to authentication service being unavailable. Actor: \"{}\"",
                    authToken.getActor().urn());
            final Map<String, Object> auditEventContext = ImmutableMap.of(
                    "remote_address", host,
                    "message", "Authentication service unavailable: " + e.getMessage()
            );
            auditEventSender.failure(authToken.getActor(), SESSION_CREATE, auditEventContext);
            throw e;
        } catch (AuthenticationException e) {
            log.info("Invalid credentials in session create request. Actor: \"{}\"", authToken.getActor().urn());
            final Map<String, Object> auditEventContext = ImmutableMap.of(
                    "remote_address", host
            );
            auditEventSender.failure(authToken.getActor(), SESSION_CREATE, auditEventContext);
            return Optional.empty();
        }
    }

    /**
     * Extract additional session attributes out of a subject's principal collection. We assume that if there is a
     * second principal, that this would be a map of session attributes.
     */
    private Map<?, ?> getSessionAttributes(Subject subject) {
        final List<?> principals = subject.getPrincipals().asList();
        if (principals.size() < 2) {
            return Collections.emptyMap();
        }
        Object sessionAttributes = principals.get(1);
        if (sessionAttributes instanceof Map) {
            return (Map<?,?>) sessionAttributes;
        }
        log.error("Unable to extract session attributes from subject. Expected <Map.class> but got <{}>.",
                sessionAttributes.getClass().getSimpleName());
        return Collections.emptyMap();
    }
}
