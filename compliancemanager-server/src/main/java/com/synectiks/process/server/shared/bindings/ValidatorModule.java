/*
 * */
package com.synectiks.process.server.shared.bindings;

import com.google.inject.AbstractModule;

import javax.validation.Validation;
import javax.validation.Validator;

public class ValidatorModule extends AbstractModule {
    @Override
    protected void configure() {
        // Validator instances are thread-safe and can be reused.
        // See: http://hibernate.org/validator/documentation/getting-started/
        //
        // The Validator instance creation is quite expensive.
        // Making this a Singleton reduced the CPU load by 50% and reduced the GC load from 5 GCs per second to 2 GCs
        // per second when running a load test of the collector registration endpoint.
        bind(Validator.class).toInstance(Validation.buildDefaultValidatorFactory().getValidator());
    }
}
