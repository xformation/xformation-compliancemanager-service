/*
 * */
package com.synectiks.process.server.shared.security;

import org.apache.shiro.authc.UsernamePasswordToken;

import com.synectiks.process.server.audit.AuditActor;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Audit actor is always the username.
 */
public class ActorAwareUsernamePasswordToken extends UsernamePasswordToken implements ActorAwareAuthenticationToken {

    public ActorAwareUsernamePasswordToken(@Nonnull String username, @Nonnull String password) {
        super(username, password);
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
    }

    @Override
    public AuditActor getActor() {
         return AuditActor.user(getUsername());
    }
}
