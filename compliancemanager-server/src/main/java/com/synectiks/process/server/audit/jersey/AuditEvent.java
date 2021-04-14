/*
 * */
package com.synectiks.process.server.audit.jersey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuditEvent {
    String actor() default "";

    String type();

    boolean captureRequestContext() default true;

    boolean captureRequestEntity() default true;

    boolean captureResponseEntity() default true;
}
