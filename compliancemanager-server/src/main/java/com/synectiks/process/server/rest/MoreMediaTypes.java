/*
 * */
package com.synectiks.process.server.rest;

import javax.ws.rs.core.MediaType;

public abstract class MoreMediaTypes {
    /**
     * A {@code String} constant representing {@value #APPLICATION_SCHEMA_JSON} media type.
     */
    public final static String APPLICATION_SCHEMA_JSON = "application/schema+json";
    /**
     * A {@link MediaType} constant representing {@value #APPLICATION_SCHEMA_JSON} media type.
     *
     * @see <a href="http://json-schema.org/latest/json-schema-core.html">JSON Schema</a>
     */
    public final static MediaType APPLICATION_SCHEMA_JSON_TYPE = new MediaType("application", "schema+json");
    /**
     * A {@code String} constant representing {@value #APPLICATION_JAVASCRIPT} media type.
     */
    public final static String APPLICATION_JAVASCRIPT = "application/javascript";
    /**
     * A {@link MediaType} constant representing {@value #APPLICATION_JAVASCRIPT} media type.
     *
     * @see <a href="https://tools.ietf.org/html/rfc4329#section-7.2">RFC 4329/a>
     */
    public final static MediaType APPLICATION_JAVASCRIPT_TYPE = new MediaType("application", "javascript");
    /**
     * A {@code String} constant representing {@value #TEXT_CSV} media type.
     */
    public final static String TEXT_CSV = "text/csv";
    /**
     * A {@link MediaType} constant representing {@value #TEXT_CSV} media type.
     */
    public final static MediaType TEXT_CSV_TYPE = new MediaType("text", "csv");
}
