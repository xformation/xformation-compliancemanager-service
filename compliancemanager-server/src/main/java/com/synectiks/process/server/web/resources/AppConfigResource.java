/*
 * */
package com.synectiks.process.server.web.resources;

import com.floreysoft.jmte.Engine;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.synectiks.process.server.Configuration;
import com.synectiks.process.server.configuration.HttpConfiguration;
import com.synectiks.process.server.rest.MoreMediaTypes;
import com.synectiks.process.server.rest.RestTools;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.util.Objects.requireNonNull;

@Path("/config.js")
public class AppConfigResource {
    private final Configuration configuration;
    private final HttpConfiguration httpConfiguration;
    private final Engine templateEngine;

    @Inject
    public AppConfigResource(Configuration configuration,
                             HttpConfiguration httpConfiguration,
                             Engine templateEngine) {
        this.configuration = requireNonNull(configuration, "configuration");
        this.httpConfiguration = requireNonNull(httpConfiguration, "httpConfiguration");
        this.templateEngine = requireNonNull(templateEngine, "templateEngine");
    }

    @GET
    @Produces(MoreMediaTypes.APPLICATION_JAVASCRIPT)
    public String get(@Context HttpHeaders headers) {
        final URL templateUrl = this.getClass().getResource("/web-interface/config.js.template");
        final String template;
        try {
            template = Resources.toString(templateUrl, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read AppConfig template while generating web interface configuration: ", e);
        }

        final URI baseUri = RestTools.buildExternalUri(headers.getRequestHeaders(), httpConfiguration.getHttpExternalUri());
        final Map<String, Object> model = ImmutableMap.of(
            "rootTimeZone", configuration.getRootTimeZone(),
            "serverUri", baseUri.resolve(HttpConfiguration.PATH_API),
            "appPathPrefix", baseUri.getPath());
        return templateEngine.transform(template, model);
    }
}
