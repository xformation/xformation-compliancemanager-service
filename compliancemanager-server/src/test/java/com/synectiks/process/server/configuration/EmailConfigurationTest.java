/*
 * */
package com.synectiks.process.server.configuration;

import com.github.joschi.jadconfig.JadConfig;
import com.github.joschi.jadconfig.RepositoryException;
import com.github.joschi.jadconfig.ValidationException;
import com.github.joschi.jadconfig.repositories.InMemoryRepository;
import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.configuration.EmailConfiguration;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class EmailConfigurationTest {
    @Test
    public void validationSucceedsIfSSLAndTLSAreDisabled() throws ValidationException, RepositoryException {
        final ImmutableMap<String, String> config = ImmutableMap.of(
                "transport_email_enabled", "true",
                "transport_email_use_tls", "false",
                "transport_email_use_ssl", "false"
        );
        final EmailConfiguration emailConfiguration = new EmailConfiguration();
        final JadConfig jadConfig = new JadConfig(new InMemoryRepository(config), emailConfiguration);
        jadConfig.process();

        assertThat(emailConfiguration.isUseSsl()).isFalse();
        assertThat(emailConfiguration.isUseTls()).isFalse();
    }

    @Test
    public void validationSucceedsIfSSLIsEnabledAndTLSIsDisabled() throws ValidationException, RepositoryException {
        final ImmutableMap<String, String> config = ImmutableMap.of(
                "transport_email_enabled", "true",
                "transport_email_use_tls", "false",
                "transport_email_use_ssl", "true"
        );
        final EmailConfiguration emailConfiguration = new EmailConfiguration();
        final JadConfig jadConfig = new JadConfig(new InMemoryRepository(config), emailConfiguration);
        jadConfig.process();

        assertThat(emailConfiguration.isUseSsl()).isTrue();
        assertThat(emailConfiguration.isUseTls()).isFalse();
    }

    @Test
    public void validationSucceedsIfSSLIsDisabledAndTLSIsEnabled() throws ValidationException, RepositoryException {
        final ImmutableMap<String, String> config = ImmutableMap.of(
                "transport_email_enabled", "true",
                "transport_email_use_tls", "true",
                "transport_email_use_ssl", "false"
        );
        final EmailConfiguration emailConfiguration = new EmailConfiguration();
        final JadConfig jadConfig = new JadConfig(new InMemoryRepository(config), emailConfiguration);
        jadConfig.process();

        assertThat(emailConfiguration.isUseSsl()).isFalse();
        assertThat(emailConfiguration.isUseTls()).isTrue();
    }

    @Test
    public void validationFailsIfSSLandTLSAreBothEnabled() {
        final ImmutableMap<String, String> config = ImmutableMap.of(
                "transport_email_enabled", "true",
                "transport_email_use_tls", "true",
                "transport_email_use_ssl", "true"
        );
        final EmailConfiguration emailConfiguration = new EmailConfiguration();
        final JadConfig jadConfig = new JadConfig(new InMemoryRepository(config), emailConfiguration);

        assertThatThrownBy(jadConfig::process)
                .isInstanceOf(ValidationException.class)
                .hasMessage("SMTP over SSL (SMTPS) and SMTP with STARTTLS cannot be used at the same time.");
    }
}