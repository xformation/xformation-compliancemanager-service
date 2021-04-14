/*
 * */
package com.synectiks.process.server.alarmcallbacks;

import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.alarmcallbacks.EmailAlarmCallback;
import com.synectiks.process.server.alerts.AlertSender;
import com.synectiks.process.server.alerts.EmailRecipients;
import com.synectiks.process.server.configuration.EmailConfiguration;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationException;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.system.NodeId;
import com.synectiks.process.server.shared.users.UserService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmailAlarmCallbackTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private AlertSender alertSender = mock(AlertSender.class);
    private NotificationService notificationService = mock(NotificationService.class);
    private NodeId nodeId = mock(NodeId.class);
    private EmailRecipients.Factory emailRecipientsFactory = mock(EmailRecipients.Factory.class);
    private UserService userService = mock(UserService.class);
    private EmailConfiguration emailConfiguration = mock(EmailConfiguration.class);
    private com.synectiks.process.server.Configuration graylogConfig = mock(com.synectiks.process.server.Configuration.class);

    private EmailAlarmCallback alarmCallback;

    @Before
    public void setUp() throws Exception {
        alarmCallback = new EmailAlarmCallback(alertSender, notificationService, nodeId, emailRecipientsFactory,
                userService, emailConfiguration, graylogConfig);
    }

    @Test
    public void checkConfigurationSucceedsWithValidConfiguration() throws Exception {
        final Map<String, Object> configMap = ImmutableMap.of(
                "sender", "graylog@example.org",
                "subject", "Graylog alert",
                "body", "foobar",
                "user_receivers", Collections.emptyList(),
                "email_receivers", Collections.emptyList()
        );
        final Configuration configuration = new Configuration(configMap);
        alarmCallback.initialize(configuration);
        alarmCallback.checkConfiguration();
    }

    @Test
    public void checkConfigurationSucceedsWithFallbackSender() throws Exception {
        final Map<String, Object> configMap = ImmutableMap.of(
                "subject", "Graylog alert",
                "body", "foobar",
                "user_receivers", Collections.emptyList(),
                "email_receivers", Collections.emptyList()
        );
        final Configuration configuration = new Configuration(configMap);
        when(emailConfiguration.getFromEmail()).thenReturn("default@sender.org");

        alarmCallback.initialize(configuration);
        alarmCallback.checkConfiguration();
    }

    @Test
    public void checkConfigurationFailsWithoutSender() throws Exception {
        final Map<String, Object> configMap = ImmutableMap.of(
                "subject", "Graylog alert",
                "body", "foobar",
                "user_receivers", Collections.emptyList(),
                "email_receivers", Collections.emptyList()
        );
        final Configuration configuration = new Configuration(configMap);
        alarmCallback.initialize(configuration);

        when(emailConfiguration.getFromEmail()).thenReturn("");

        expectedException.expect(ConfigurationException.class);
        expectedException.expectMessage("Sender or subject are missing or invalid.");

        alarmCallback.checkConfiguration();
    }

    @Test
    public void checkConfigurationFailsWithoutSubject() throws Exception {
        final Map<String, Object> configMap = ImmutableMap.of(
                "sender", "graylog@example.org",
                "body", "foobar",
                "user_receivers", Collections.emptyList(),
                "email_receivers", Collections.emptyList()
        );
        final Configuration configuration = new Configuration(configMap);
        alarmCallback.initialize(configuration);

        expectedException.expect(ConfigurationException.class);
        expectedException.expectMessage("Sender or subject are missing or invalid.");

        alarmCallback.checkConfiguration();
    }

    @Test
    public void getEnrichedRequestedConfigurationReturnsUsersListIncludingAdminUser() throws Exception {
        final String userName = "admin";
        when(graylogConfig.getRootUsername()).thenReturn(userName);
        final ConfigurationRequest configuration = alarmCallback.getEnrichedRequestedConfiguration();
        assertThat(configuration.containsField("user_receivers")).isTrue();
        final Map<String, String> users = configuration.getField("user_receivers").getAdditionalInformation().get("values");
        assertThat(users).containsEntry(userName, userName);
    }
}