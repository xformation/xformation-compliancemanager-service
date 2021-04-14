/*
 * */
package com.synectiks.process.server.shared.security;

import org.apache.shiro.authz.aop.PermissionAnnotationHandler;
import org.apache.shiro.subject.Subject;

import static java.util.Objects.requireNonNull;

public class ContextAwarePermissionAnnotationHandler extends PermissionAnnotationHandler {
    private final ShiroSecurityContext context;

    public ContextAwarePermissionAnnotationHandler(ShiroSecurityContext context) {
        this.context = requireNonNull(context);
    }

    @Override
    protected Subject getSubject() {
        return context.getSubject();
    }
}
