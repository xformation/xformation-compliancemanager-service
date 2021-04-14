/*
 * */
package com.synectiks.process.common.testing.inject;

import com.google.inject.name.Names;
import com.synectiks.process.server.plugin.inject.Graylog2Module;

public class TestPasswordSecretModule extends Graylog2Module {
    public static final String TEST_PASSWORD_SECRET = "f9a79178-c949-446d-b0ae-c6d5d9a40ba8";

    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("password_secret")).toInstance(TEST_PASSWORD_SECRET);
    }
}
