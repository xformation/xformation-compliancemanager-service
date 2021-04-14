/*
 * */
package com.synectiks.process.common.testing.mongodb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used with the {@link MongoDBInstance} role to load the given MongoDB data fixtures into the
 * database by using {@link MongoDBFixtureImporter}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface MongoDBFixtures {
    String[] value();
}
