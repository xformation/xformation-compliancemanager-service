/*
 * */
package com.synectiks.process.server.periodical;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.synectiks.process.server.notifications.Notification;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.notifications.NotificationServiceImpl;
import com.synectiks.process.server.periodical.ESVersionCheckPeriodical;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.plugin.periodical.Periodical;
import com.synectiks.process.server.storage.versionprobe.VersionProbe;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ESVersionCheckPeriodicalTest {
    private VersionProbe versionProbe;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        this.versionProbe = mock(VersionProbe.class);
        this.notificationService = mock(NotificationServiceImpl.class);
        when(this.notificationService.build()).thenCallRealMethod();
        when(this.notificationService.buildNow()).thenCallRealMethod();
    }

    @Test
    void doesNotRunIfVersionOverrideIsSet() {
        createPeriodical(Version.from(8, 0, 0), Version.from(7, 0, 0)).doRun();

        verifyNoInteractions(notificationService);
    }

    @Test
    void doesNotDoAnythingIfVersionWasNotProbed() {
        returnProbedVersion(null);

        createPeriodical(Version.from(8, 0, 0)).doRun();

        verifyNoInteractions(notificationService);
    }

    @Test
    void createsNotificationIfCurrentVersionIsIncompatibleWithInitialOne() {
        returnProbedVersion(Version.from(9, 2, 3));

        createPeriodical(Version.from(8, 1, 2)).doRun();

        assertNotificationWasRaised();
    }

    @Test
    void createsNotificationIfCurrentVersionIncompatiblyOlderThanInitialOne() {
        returnProbedVersion(Version.from(6, 8, 1));

        createPeriodical(Version.from(8, 1, 2)).doRun();

        assertNotificationWasRaised();
    }

    @Test
    void fixesNotificationIfCurrentVersionIsIncompatibleWithInitialOne() {
        returnProbedVersion(Version.from(8, 2, 3));

        createPeriodical(Version.from(8, 1, 2)).doRun();

        assertNotificationWasFixed();
    }

    private void assertNotificationWasFixed() {
        final ArgumentCaptor<Notification.Type> captor = ArgumentCaptor.forClass(Notification.Type.class);
        verify(notificationService, times(1)).fixed(captor.capture());

        assertThat(captor.getValue()).isEqualTo(Notification.Type.ES_VERSION_MISMATCH);
    }

    private void assertNotificationWasRaised() {
        final ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationService, times(1)).publishIfFirst(captor.capture());

        assertThat(captor.getValue().getType()).isEqualTo(Notification.Type.ES_VERSION_MISMATCH);
    }

    private void returnProbedVersion(@Nullable Version probedVersion) {
        when(versionProbe.probe(anyCollection())).thenReturn(Optional.ofNullable(probedVersion));
    }

    private Periodical createPeriodical(Version initialVersion) {
        return new ESVersionCheckPeriodical(initialVersion, null, Collections.emptyList(), versionProbe, notificationService);
    }

    private Periodical createPeriodical(Version initialVersion, @Nullable Version versionOverride) {
        return new ESVersionCheckPeriodical(initialVersion, versionOverride, Collections.emptyList(), versionProbe, notificationService);
    }
}
