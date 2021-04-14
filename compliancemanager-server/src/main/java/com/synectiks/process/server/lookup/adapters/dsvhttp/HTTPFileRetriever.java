/*
 * */
package com.synectiks.process.server.lookup.adapters.dsvhttp;

import com.google.common.collect.ImmutableMap;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class HTTPFileRetriever {
    private final AtomicReference<Map<String, String>> lastLastModified = new AtomicReference<>(Collections.emptyMap());
    private final OkHttpClient client;

    @Inject
    public HTTPFileRetriever(OkHttpClient httpClient) {
        this.client = httpClient.newBuilder()
                .followRedirects(true)
                .followSslRedirects(true)
                .build();
    }

    /**
     * Request file. No "If-Modified-Since" header will be sent so the file will be fetched again, even if hasn't been
     * modified since the last fetch.
     */
    public Optional<String> fetchFile(String url) throws IOException {
        return fetchFile(url, false);
    }

    /**
     * Request file by sending an "If-Modified-Since" header so that the file won't be fetched if it hasn't been
     * modified since the last request.
     */
    public Optional<String> fetchFileIfNotModified(String url) throws IOException {
        return fetchFile(url, true);
    }

    private Optional<String> fetchFile(String url, boolean addIfModifiedSinceHeader) throws IOException {
        final Request.Builder requestBuilder = new Request.Builder()
                .get()
                .url(url)
                .header("User-Agent", "alertmanager (server)");
        if (addIfModifiedSinceHeader) {
            final String lastModified = this.lastLastModified.get().get(url);
            if (lastModified != null) {
                requestBuilder.header("If-Modified-Since", lastModified);
            }
        }
        final Call request = client.newCall(requestBuilder.build());

        try (final Response response = request.execute()) {
            if (response.isSuccessful()) {
                final String lastModifiedHeader = response.header("Last-Modified", DateTime.now(DateTimeZone.UTC).toString());
                final Map<String, String> newLastModified = new HashMap<>(this.lastLastModified.get());
                newLastModified.put(url, lastModifiedHeader);
                this.lastLastModified.set(ImmutableMap.copyOf(newLastModified));

                if (response.body() != null) {
                    final String body = response.body().string();
                    return Optional.ofNullable(body);
                }
            } else {
                if (response.code() != 304) {
                    throw new IOException("Request failed: " + response.message());
                }
            }
        }

        return Optional.empty();
    }
}
