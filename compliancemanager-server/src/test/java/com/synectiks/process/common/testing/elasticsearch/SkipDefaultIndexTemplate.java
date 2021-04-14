/*
 * */
package com.synectiks.process.common.testing.elasticsearch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used if the {@link ElasticsearchBaseTest} should skip the creation of the default
 * index templates. That can be helpful if you want to run tests against pristine indices without any settings
 * or mapping configuration.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SkipDefaultIndexTemplate {
}
