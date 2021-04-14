/*
 * */
package com.synectiks.process.server.shared.security;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActorAwareUsernamePasswordTokenFactory implements ActorAwareAuthenticationTokenFactory {

    @Override
    public ActorAwareAuthenticationToken forRequestBody(JsonNode jsonBody) {

        String missingProperties = Stream.of("username", "password")
                .filter(key -> jsonBody.get(key) == null || jsonBody.get(key).asText().isEmpty())
                .collect(Collectors.joining(", "));

        if (!missingProperties.isEmpty()) {
            throw new IllegalArgumentException("Missing required properties: " + missingProperties + ".");
        }

        String username = jsonBody.get("username").asText();
        String password = jsonBody.get("password").asText();

        return new ActorAwareUsernamePasswordToken(username, password);
    }
}
