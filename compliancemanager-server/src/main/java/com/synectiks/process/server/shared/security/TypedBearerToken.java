/*
 * */
package com.synectiks.process.server.shared.security;

import org.apache.shiro.authc.BearerToken;

import com.synectiks.process.server.audit.AuditActor;

public class TypedBearerToken extends BearerToken implements ActorAwareAuthenticationToken {
    private final AuditActor actor;
    private final String type;

    public TypedBearerToken(String token, AuditActor actor, String type) {
        super(token);
        this.actor = actor;
        this.type = type;
    }

    @Override
    public AuditActor getActor() {
         return actor;
    }

    public String getType() {
        return type;
    }
}
