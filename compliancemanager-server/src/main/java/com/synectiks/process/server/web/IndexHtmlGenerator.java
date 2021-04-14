/*
 * */
package com.synectiks.process.server.web;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Implementations provide HTML content for an "index.html" file. This file will be served to browser clients.
 */
public interface IndexHtmlGenerator {
    /**
     * Get the HTML content.
     *
     * @param headers the HTTP request headers of the web request
     * @return the HTML string
     */
    String get(MultivaluedMap<String, String> headers);
}
