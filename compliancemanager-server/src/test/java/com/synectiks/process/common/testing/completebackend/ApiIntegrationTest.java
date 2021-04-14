/*
 * */
package com.synectiks.process.common.testing.completebackend;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation can be used to extend JUnit 5 test classes or methods ,
 * which manages instantiating the backend and allows access to necessary settings like the URI necessary
 * to reach the API.
 */
@Target({TYPE, METHOD})
@ExtendWith(GraylogBackendExtension.class)
@Retention(RUNTIME)
@Tag("integration")
public @interface ApiIntegrationTest {
    Lifecycle serverLifecycle() default Lifecycle.METHOD;

    int[] extraPorts() default {};

    Class<? extends ElasticsearchInstanceFactory> elasticsearchFactory();
}
