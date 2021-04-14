/*
 * */
package com.synectiks.process.server.shared.security;

import org.apache.shiro.authc.AuthenticationToken;

import com.synectiks.process.server.audit.AuditActor;

public interface ActorAwareAuthenticationToken extends AuthenticationToken {
    AuditActor getActor();
}
