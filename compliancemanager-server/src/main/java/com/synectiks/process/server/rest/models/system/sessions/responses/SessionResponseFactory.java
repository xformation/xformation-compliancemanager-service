/*
 * */
package com.synectiks.process.server.rest.models.system.sessions.responses;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.shiro.session.Session;

/**
 * Factory to create a JSON response for a given session. A plugin may provide a custom implementation, if additional
 * attributes are required in the response.
 */
public interface SessionResponseFactory {
    /**
     * Create a JSON response for the given session.
     */
    JsonNode forSession(Session session);
}
