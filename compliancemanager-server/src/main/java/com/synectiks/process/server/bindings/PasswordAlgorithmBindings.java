/*
 * */
package com.synectiks.process.server.bindings;

import com.google.inject.multibindings.MapBinder;
import com.synectiks.process.server.bindings.providers.DefaultPasswordAlgorithmProvider;
import com.synectiks.process.server.plugin.inject.Graylog2Module;
import com.synectiks.process.server.plugin.security.PasswordAlgorithm;
import com.synectiks.process.server.security.hashing.BCryptPasswordAlgorithm;
import com.synectiks.process.server.security.hashing.SHA1HashPasswordAlgorithm;
import com.synectiks.process.server.users.DefaultPasswordAlgorithm;

public class PasswordAlgorithmBindings extends Graylog2Module {
    @Override
    protected void configure() {
        bindPasswordAlgorithms();
    }

    private void bindPasswordAlgorithms() {
        MapBinder<String, PasswordAlgorithm> passwordAlgorithms = MapBinder.newMapBinder(binder(), String.class, PasswordAlgorithm.class);
        passwordAlgorithms.addBinding("sha-1").to(SHA1HashPasswordAlgorithm.class);
        passwordAlgorithms.addBinding("bcrypt").to(BCryptPasswordAlgorithm.class);

        bind(PasswordAlgorithm.class).annotatedWith(DefaultPasswordAlgorithm.class).toProvider(DefaultPasswordAlgorithmProvider.class);
    }
}
