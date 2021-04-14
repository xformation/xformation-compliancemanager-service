/*
 * */
package com.synectiks.process.server.periodical;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.process.server.notifications.Notification;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.plugin.periodical.Periodical;
import com.synectiks.process.server.storage.ElasticsearchVersion;
import com.synectiks.process.server.storage.versionprobe.VersionProbe;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public class ESVersionCheckPeriodical extends Periodical {
    private static final Logger LOG = LoggerFactory.getLogger(ESVersionCheckPeriodical.class);
    private final Version initialElasticsearchVersion;
    private final Optional<Version> versionOverride;
    private final List<URI> elasticsearchHosts;
    private final VersionProbe versionProbe;
    private final NotificationService notificationService;

    @Inject
    public ESVersionCheckPeriodical(@ElasticsearchVersion Version elasticsearchVersion,
                                    @Named("elasticsearch_version") @Nullable Version versionOverride,
                                    @Named("elasticsearch_hosts") List<URI> elasticsearchHosts,
                                    VersionProbe versionProbe,
                                    NotificationService notificationService) {
        this.initialElasticsearchVersion = elasticsearchVersion;
        this.versionOverride = Optional.ofNullable(versionOverride);
        this.elasticsearchHosts = elasticsearchHosts;
        this.versionProbe = versionProbe;
        this.notificationService = notificationService;
    }

    @Override
    public boolean runsForever() {
        return false;
    }

    @Override
    public boolean stopOnGracefulShutdown() {
        return true;
    }

    @Override
    public boolean masterOnly() {
        return true;
    }

    @Override
    public boolean startOnThisNode() {
        return true;
    }

    @Override
    public boolean isDaemon() {
        return true;
    }

    @Override
    public int getInitialDelaySeconds() {
        return 0;
    }

    @Override
    public int getPeriodSeconds() {
        return 30;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public void doRun() {
        if (versionOverride.isPresent()) {
            LOG.debug("Elasticsearch version is set manually. Not running check.");
            return;
        }

        final Optional<Version> probedVersion = this.versionProbe.probe(this.elasticsearchHosts);

        probedVersion.ifPresent(version -> {
            if (compatible(this.initialElasticsearchVersion, version)) {
                notificationService.fixed(Notification.Type.ES_VERSION_MISMATCH);
            } else {
                LOG.warn("Elasticsearch version currently running ({}) is incompatible with the one alertmanager was started " +
                        "with ({}) - a restart is required!", version, initialElasticsearchVersion);
                final Notification notification = notificationService.buildNow()
                        .addType(Notification.Type.ES_VERSION_MISMATCH)
                        .addSeverity(Notification.Severity.URGENT)
                        .addDetail("initial_version", initialElasticsearchVersion.toString())
                        .addDetail("current_version", version.toString());
                notificationService.publishIfFirst(notification);
            }
        });
    }

    private boolean compatible(Version initialElasticsearchMajorVersion, Version version) {
        return initialElasticsearchMajorVersion.getVersion().getMajorVersion() == version.getVersion().getMajorVersion();
    }
}
