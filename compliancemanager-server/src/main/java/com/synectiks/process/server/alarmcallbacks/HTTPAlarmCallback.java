/*
 * */
package com.synectiks.process.server.alarmcallbacks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.alarms.callbacks.AlarmCallback;
import com.synectiks.process.server.plugin.alarms.callbacks.AlarmCallbackConfigurationException;
import com.synectiks.process.server.plugin.alarms.callbacks.AlarmCallbackException;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.configuration.ConfigurationException;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.configuration.fields.ConfigurationField;
import com.synectiks.process.server.plugin.configuration.fields.TextField;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.system.urlwhitelist.UrlWhitelistService;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class HTTPAlarmCallback implements AlarmCallback {
    private static final String CK_URL = "url";
    private static final MediaType CONTENT_TYPE = MediaType.parse(APPLICATION_JSON);

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private Configuration configuration;
    private final UrlWhitelistService whitelistService;

    @Inject
    public HTTPAlarmCallback(final OkHttpClient httpClient, final ObjectMapper objectMapper,
            UrlWhitelistService whitelistService) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.whitelistService = whitelistService;
    }

    @Override
    public void initialize(final Configuration config) throws AlarmCallbackConfigurationException {
        this.configuration = config;
    }

    @Override
    public void call(final Stream stream, final AlertCondition.CheckResult result) throws AlarmCallbackException {
        final Map<String, Object> event = Maps.newHashMap();
        event.put("stream", stream);
        event.put("check_result", result);

        final byte[] body;
        try {
            body = objectMapper.writeValueAsBytes(event);
        } catch (JsonProcessingException e) {
            throw new AlarmCallbackException("Unable to serialize alarm", e);
        }

        final String url = configuration.getString(CK_URL);
        final HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            throw new AlarmCallbackException("Malformed URL: " + url);
        }

        if (!whitelistService.isWhitelisted(url)) {
            throw new AlarmCallbackException("URL <" + url + "> is not whitelisted.");
        }

        final Request request = new Request.Builder()
                .url(httpUrl)
                .post(RequestBody.create(CONTENT_TYPE, body))
                .build();
        try (final Response r = httpClient.newCall(request).execute()) {
            if (!r.isSuccessful()) {
                throw new AlarmCallbackException("Expected successful HTTP response [2xx] but got [" + r.code() + "].");
            }
        } catch (IOException e) {
            throw new AlarmCallbackException(e.getMessage(), e);
        }
    }

    @Override
    public ConfigurationRequest getRequestedConfiguration() {
        final ConfigurationRequest configurationRequest = new ConfigurationRequest();
        configurationRequest.addField(new TextField(CK_URL,
                "URL",
                "https://example.org/alerts",
                "The URL to POST to when an alert is triggered",
                ConfigurationField.Optional.NOT_OPTIONAL));

        return configurationRequest;
    }

    @Override
    public String getName() {
        return "HTTP Alarm Callback [Deprecated]";
    }

    @Override
    public Map<String, Object> getAttributes() {
        return configuration.getSource();
    }

    @Override
    public void checkConfiguration() throws ConfigurationException {
        final String url = configuration.getString(CK_URL);
        if (isNullOrEmpty(url)) {
            throw new ConfigurationException("URL parameter is missing.");
        }

        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new ConfigurationException("Malformed URL '" + url + "'", e);
        }

        if (!whitelistService.isWhitelisted(url)) {
            throw new ConfigurationException("URL <" + url + "> is not whitelisted.");
        }
    }
}
