/*
 * */
package com.synectiks.process.server.migrations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.contentpacks.ContentPackPersistenceService;
import com.synectiks.process.server.contentpacks.ContentPackService;
import com.synectiks.process.server.contentpacks.exceptions.ContentPackException;
import com.synectiks.process.server.contentpacks.model.ContentPack;
import com.synectiks.process.server.plugin.cluster.ClusterConfigService;

import org.graylog.autovalue.WithBeanGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Collections;

public class V20191219090834_AddSourcesPage extends Migration {
    private static final Logger LOG = LoggerFactory.getLogger(V20191219090834_AddSourcesPage.class);

    private final ContentPackService contentPackService;
    private final ObjectMapper objectMapper;
    private final ClusterConfigService configService;
    private final ContentPackPersistenceService contentPackPersistenceService;

    @Inject
    public V20191219090834_AddSourcesPage(final ContentPackPersistenceService contentPackPersistenceService,
                                          final ContentPackService contentPackService,
                                          final ObjectMapper objectMapper,
                                          final ClusterConfigService clusterConfigService) {
        this.contentPackService = contentPackService;
        this.objectMapper = objectMapper;
        this.contentPackPersistenceService = contentPackPersistenceService;
        this.configService = clusterConfigService;
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.parse("2019-12-19T09:08:34Z");
    }

    @Override
    public void upgrade() {
        if (configService.get(V20191219090834_AddSourcesPage.MigrationCompleted.class) != null) {
            LOG.debug("Migration already completed.");
            return;
        }

        try {
            final URL contentPackURL = V20191219090834_AddSourcesPage.class
                    .getResource("V20191219090834_AddSourcesPage_Content_Pack.json");
            final ContentPack contentPack = this.objectMapper.readValue(contentPackURL, ContentPack.class);
            final ContentPack pack = this.contentPackPersistenceService.insert(contentPack)
                    .orElseThrow(() -> {
                        configService.write(V20191219090834_AddSourcesPage.MigrationCompleted.create(contentPack.id().toString()));
                        return new ContentPackException("Content pack " + contentPack.id() + " with this revision " + contentPack.revision() + " already found!");
                    });

            contentPackService.installContentPack(pack, Collections.emptyMap(), "Add Sources Page", "admin");

            configService.write(V20191219090834_AddSourcesPage.MigrationCompleted.create(pack.id().toString()));
        } catch (Exception e) {
            throw new RuntimeException("Could not install Source Page Content Pack.", e);
        }
    }

    @JsonAutoDetect
    @AutoValue
    @WithBeanGetter
    public static abstract class MigrationCompleted {
        @JsonProperty("content_pack_id")
        public abstract String contentPackId();

        @JsonCreator
        public static V20191219090834_AddSourcesPage.MigrationCompleted create(@JsonProperty("content_pack_id") final String contentPackId) {
            return new AutoValue_V20191219090834_AddSourcesPage_MigrationCompleted(contentPackId);
        }
    }
}
