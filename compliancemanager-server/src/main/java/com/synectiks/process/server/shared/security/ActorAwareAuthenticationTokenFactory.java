/*
 * */
package com.synectiks.process.server.shared.security;

import com.fasterxml.jackson.databind.JsonNode;

public interface ActorAwareAuthenticationTokenFactory {
    /**
     * Create an authentication token for the given JsonNode
     * @throws IllegalArgumentException if the required properties are missing in the input (or otherwise not valid)
     */
    ActorAwareAuthenticationToken forRequestBody(JsonNode jsonNode);
}
