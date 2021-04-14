/*
 * */
package com.synectiks.process.common.events.notifications.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.synectiks.process.common.events.notifications.EventNotification;
import com.synectiks.process.common.events.notifications.EventNotificationContext;
import com.synectiks.process.common.events.notifications.EventNotificationModelData;
import com.synectiks.process.common.events.notifications.EventNotificationService;
import com.synectiks.process.common.events.notifications.NotificationTestData;
import com.synectiks.process.common.events.notifications.PermanentEventNotificationException;
import com.synectiks.process.common.events.notifications.TemporaryEventNotificationException;
import com.synectiks.process.server.plugin.MessageSummary;
import com.synectiks.process.server.system.urlwhitelist.UrlWhitelistNotificationService;
import com.synectiks.process.server.system.urlwhitelist.UrlWhitelistService;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class HTTPEventNotification implements EventNotification {
    public interface Factory extends EventNotification.Factory {
        @Override
        HTTPEventNotification create();
    }

    private static final Logger LOG = LoggerFactory.getLogger(HTTPEventNotification.class);

    private static final MediaType CONTENT_TYPE = MediaType.parse(APPLICATION_JSON);

    private final EventNotificationService notificationCallbackService;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;
    private final UrlWhitelistService whitelistService;
    private final UrlWhitelistNotificationService urlWhitelistNotificationService;

    @Inject
    public HTTPEventNotification(EventNotificationService notificationCallbackService, ObjectMapper objectMapper,
            final OkHttpClient httpClient, UrlWhitelistService whitelistService,
            UrlWhitelistNotificationService urlWhitelistNotificationService) {
        this.notificationCallbackService = notificationCallbackService;
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
        this.whitelistService = whitelistService;
        this.urlWhitelistNotificationService = urlWhitelistNotificationService;
    }

    @Override
    public void execute(EventNotificationContext ctx) throws TemporaryEventNotificationException, PermanentEventNotificationException {
        final HTTPEventNotificationConfig config = (HTTPEventNotificationConfig) ctx.notificationConfig();
        final HttpUrl httpUrl = HttpUrl.parse(config.url());

        if (httpUrl == null) {
            throw new TemporaryEventNotificationException(
                    "Malformed URL: <" + config.url() + "> in notification <" + ctx.notificationId() + ">");
        }

        ImmutableList<MessageSummary> backlog = notificationCallbackService.getBacklogForEvent(ctx);
        final EventNotificationModelData model = EventNotificationModelData.of(ctx, backlog);

        if (!whitelistService.isWhitelisted(config.url())) {
            if (!NotificationTestData.TEST_NOTIFICATION_ID.equals(ctx.notificationId())) {
                publishSystemNotificationForWhitelistFailure(config.url(), model.eventDefinitionTitle());
            }
            throw new TemporaryEventNotificationException("URL <" + config.url() + "> is not whitelisted.");
        }

        final byte[] body;
        try {
            body = objectMapper.writeValueAsBytes(model);
        } catch (JsonProcessingException e) {
            throw new PermanentEventNotificationException("Unable to serialize notification", e);
        }
        final Request request = new Request.Builder()
                .url(httpUrl)
                .post(RequestBody.create(CONTENT_TYPE, body))
                .build();

        LOG.debug("Requesting HTTP endpoint at <{}> in notification <{}>",
                config.url(),
                ctx.notificationId());

        try (final Response r = httpClient.newCall(request).execute()) {
            if (!r.isSuccessful()) {
                throw new PermanentEventNotificationException(
                        "Expected successful HTTP response [2xx] but got [" + r.code() + "]. " + config.url());
            }
        } catch (IOException e) {
            throw new PermanentEventNotificationException(e.getMessage());
        }
    }

    private void publishSystemNotificationForWhitelistFailure(String url, String eventNotificationTitle) {
        final String description = "The alert notification \"" + eventNotificationTitle +
                "\" is trying to access a URL which is not whitelisted. Please check your configuration. [url: \"" +
                url + "\"]";
        urlWhitelistNotificationService.publishWhitelistFailure(description);
    }
}
