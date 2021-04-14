/*
 * */
package com.synectiks.process.server.shared.security;

import org.apache.shiro.subject.Subject;

import java.security.Principal;

import static java.util.Objects.requireNonNull;

public class ShiroPrincipal implements Principal {
    private final Subject subject;

    public ShiroPrincipal(Subject subject) {
        this.subject = requireNonNull(subject);
    }

    @Override
    public String getName() {
        final Object principal = subject.getPrincipal();
        return principal == null ? null : principal.toString();
    }

    public Subject getSubject() {
        return subject;
    }

    @Override
    public String toString() {

        return "ShiroPrincipal[" + getName() + "]";

    }
}
