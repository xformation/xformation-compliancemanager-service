/*
 * */
package com.synectiks.process.server.rest.resources.tools;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.ConfigurationException;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.inputs.extractors.JsonExtractor;
import com.synectiks.process.server.plugin.inputs.Converter;
import com.synectiks.process.server.plugin.inputs.Extractor;
import com.synectiks.process.server.rest.models.tools.requests.JsonTestRequest;
import com.synectiks.process.server.rest.models.tools.responses.JsonTesterResponse;
import com.synectiks.process.server.shared.rest.resources.RestResource;

import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Map;

@RequiresAuthentication
@Path("/tools/json_tester")
@Produces(MediaType.APPLICATION_JSON)
public class JsonTesterResource extends RestResource {
    @GET
    @Timed
    public JsonTesterResponse get(@QueryParam("string") @NotEmpty String string,
                                  @QueryParam("flatten") @DefaultValue("false") boolean flatten,
                                  @QueryParam("list_separator") @NotEmpty String listSeparator,
                                  @QueryParam("key_separator") @NotEmpty String keySeparator,
                                  @QueryParam("replace_key_whitespace") boolean replaceKeyWhitespace,
                                  @QueryParam("key_whitespace_replacement") String keyWhitespaceReplacement,
                                  @QueryParam("key_prefix") String keyPrefix,
                                  @QueryParam("kv_separator") @NotEmpty String kvSeparator) {
        return testJsonExtractor(string, flatten, listSeparator, keySeparator, kvSeparator, replaceKeyWhitespace, keyWhitespaceReplacement, keyPrefix);
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @NoAuditEvent("only used for testing JSON extractor")
    public JsonTesterResponse post(@Valid @NotNull JsonTestRequest r) {
        return testJsonExtractor(r.string(), r.flatten(), r.listSeparator(), r.keySeparator(), r.kvSeparator(), r.replaceKeyWhitespace(), r.keyWhitespaceReplacement(), r.keyPrefix());
    }

    private JsonTesterResponse testJsonExtractor(String testString,
                                                 boolean flatten,
                                                 String listSeparator,
                                                 String keySeparator,
                                                 String kvSeparator,
                                                 boolean replaceKeyWhitespace,
                                                 String keyWhitespaceReplacement,
                                                 String keyPrefix) {
        final Map<String, Object> config = ImmutableMap.<String, Object>builder()
                .put("flatten", flatten)
                .put("list_separator", listSeparator)
                .put("key_separator", keySeparator)
                .put("kv_separator", kvSeparator)
                .put("replace_key_whitespace", replaceKeyWhitespace)
                .put("key_whitespace_replacement", keyWhitespaceReplacement)
                .put("key_prefix", keyPrefix)
                .build();
        final JsonExtractor extractor;
        try {
            extractor = new JsonExtractor(
                    new MetricRegistry(), "test", "Test", 0L, Extractor.CursorStrategy.COPY, "test", "test",
                    config, getCurrentUser().getName(), Collections.<Converter>emptyList(), Extractor.ConditionType.NONE, ""
            );
        } catch (Extractor.ReservedFieldException e) {
            throw new BadRequestException("Trying to overwrite a reserved message field", e);
        } catch (ConfigurationException e) {
            throw new BadRequestException("Invalid extractor configuration", e);
        }

        final Map<String, Object> result = extractor.extractJson(testString);
        return JsonTesterResponse.create(result, flatten, listSeparator, keySeparator, kvSeparator, testString);
    }
}
