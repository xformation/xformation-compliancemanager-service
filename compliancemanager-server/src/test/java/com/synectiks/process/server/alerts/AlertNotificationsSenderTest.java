/*
 * */
package com.synectiks.process.server.alerts;

import com.google.common.collect.ImmutableList;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfiguration;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfigurationService;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackFactory;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackHistoryService;
import com.synectiks.process.server.alerts.AbstractAlertCondition;
import com.synectiks.process.server.alerts.Alert;
import com.synectiks.process.server.alerts.AlertNotificationsSender;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.alarms.callbacks.AlarmCallback;
import com.synectiks.process.server.plugin.streams.Stream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AlertNotificationsSenderTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private AlertNotificationsSender alertNotificationsSender;

    @Mock
    private AlarmCallbackConfigurationService alarmCallbackConfigurationService;
    @Mock
    private AlarmCallbackFactory alarmCallbackFactory;
    @Mock
    private AlarmCallbackHistoryService alarmCallbackHistoryService;

    @Before
    public void setUp() throws Exception {
        this.alertNotificationsSender = new AlertNotificationsSender(alarmCallbackConfigurationService, alarmCallbackFactory, alarmCallbackHistoryService);
    }

    @Test
    public void executeStreamWithNotifications() throws Exception {
        final Stream stream = mock(Stream.class);
        final Alert alert = mock(Alert.class);
        final AlertCondition alertCondition = mock(AlertCondition.class);
        final AlertCondition.CheckResult positiveCheckResult = new AbstractAlertCondition.CheckResult(true, alertCondition, "Mocked positive CheckResult", Tools.nowUTC(), Collections.emptyList());

        final AlarmCallbackConfiguration alarmCallbackConfiguration = mock(AlarmCallbackConfiguration.class);
        when(alarmCallbackConfigurationService.getForStream(eq(stream))).thenReturn(ImmutableList.of(alarmCallbackConfiguration));
        final AlarmCallback alarmCallback = mock(AlarmCallback.class);
        when(alarmCallbackFactory.create(eq(alarmCallbackConfiguration))).thenReturn(alarmCallback);

        alertNotificationsSender.send(positiveCheckResult, stream, alert, alertCondition);

        final ArgumentCaptor<Stream> streamCaptor = ArgumentCaptor.forClass(Stream.class);
        final ArgumentCaptor<AlertCondition.CheckResult> checkResultCaptor = ArgumentCaptor.forClass(AlertCondition.CheckResult.class);
        verify(alarmCallback, times(1)).call(streamCaptor.capture(), checkResultCaptor.capture());
        assertThat(streamCaptor.getValue()).isEqualTo(stream);
        assertThat(checkResultCaptor.getValue()).isEqualTo(positiveCheckResult);

        final ArgumentCaptor<AlarmCallbackConfiguration> alarmCallbackConfigurationCaptor = ArgumentCaptor.forClass(AlarmCallbackConfiguration.class);
        final ArgumentCaptor<Alert> alertCaptor = ArgumentCaptor.forClass(Alert.class);
        final ArgumentCaptor<AlertCondition> alertConditionCaptor = ArgumentCaptor.forClass(AlertCondition.class);
        verify(alarmCallbackHistoryService, times(1)).success(alarmCallbackConfigurationCaptor.capture(), alertCaptor.capture(), alertConditionCaptor.capture());

        assertThat(alarmCallbackConfigurationCaptor.getValue()).isEqualTo(alarmCallbackConfiguration);
        assertThat(alertCaptor.getValue()).isEqualTo(alert);
        assertThat(alertConditionCaptor.getValue()).isEqualTo(alertCondition);
    }

}