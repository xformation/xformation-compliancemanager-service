/*
 * */
package com.synectiks.process.server.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.synectiks.process.server.cluster.Node;
import com.synectiks.process.server.security.realm.SessionAuthenticator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.glassfish.jersey.client.filter.CsrfProtectionFilter;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Inject;

public class RemoteInterfaceProvider {
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;

    @Inject
    public RemoteInterfaceProvider(ObjectMapper objectMapper,
                                   OkHttpClient okHttpClient) {
        this.objectMapper = objectMapper;
        this.okHttpClient = okHttpClient;
    }

    public <T> T get(Node node, final String authorizationToken, Class<T> interfaceClass) {
        final OkHttpClient okHttpClient = this.okHttpClient.newBuilder()
                .addInterceptor(chain -> {
                    final Request original = chain.request();

                    Request.Builder builder = original.newBuilder()
                            .header(HttpHeaders.ACCEPT, MediaType.JSON_UTF_8.toString())
                            .header(CsrfProtectionFilter.HEADER_NAME, "Graylog Server")
                            .method(original.method(), original.body());

                    if (authorizationToken != null) {
                        builder = builder
                                // forward the authentication information of the current user
                                .header(HttpHeaders.AUTHORIZATION, authorizationToken)
                                // do not extend the users session with proxied requests
                                .header(SessionAuthenticator.X_GRAYLOG_NO_SESSION_EXTENSION, "true");
                    }

                    return chain.proceed(builder.build());
                })
                .build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(node.getTransportAddress())
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(okHttpClient)
                .build();

        return retrofit.create(interfaceClass);
    }

    public <T> T get(Node node, Class<T> interfaceClass) {
        return get(node, null, interfaceClass);
    }
}
