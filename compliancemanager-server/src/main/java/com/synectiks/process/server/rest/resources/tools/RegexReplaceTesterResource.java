/*
 * */
package com.synectiks.process.server.rest.resources.tools;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableMap;
import com.synectiks.process.server.ConfigurationException;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.inputs.extractors.RegexReplaceExtractor;
import com.synectiks.process.server.plugin.inputs.Converter;
import com.synectiks.process.server.plugin.inputs.Extractor;
import com.synectiks.process.server.rest.models.tools.requests.RegexReplaceTestRequest;
import com.synectiks.process.server.rest.models.tools.responses.RegexReplaceTesterResponse;
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
@Path("/tools/regex_replace_tester")
public class RegexReplaceTesterResource extends RestResource {
    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    public RegexReplaceTesterResponse regexTester(@QueryParam("regex") @NotEmpty String regex,
                                                  @QueryParam("replacement") @NotNull String replacement,
                                                  @QueryParam("replace_all") @DefaultValue("false") boolean replaceAll,
                                                  @QueryParam("string") @NotNull String string) {
        return testRegexReplaceExtractor(string, regex, replacement, replaceAll);
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NoAuditEvent("only used to test regex replace extractor")
    public RegexReplaceTesterResponse testRegex(@Valid @NotNull RegexReplaceTestRequest r) {
        return testRegexReplaceExtractor(r.string(), r.regex(), r.replacement(), r.replaceAll());
    }

    private RegexReplaceTesterResponse testRegexReplaceExtractor(String example, String regex, String replacement, boolean replaceAll) {
        final Map<String, Object> config = ImmutableMap.<String, Object>of(
                "regex", regex,
                "replacement", replacement,
                "replace_all", replaceAll
        );
        final RegexReplaceExtractor extractor;
        try {
            extractor = new RegexReplaceExtractor(
                    new MetricRegistry(), "test", "Test", 0L, Extractor.CursorStrategy.COPY, "test", "test",
                    config, getCurrentUser().getName(), Collections.<Converter>emptyList(), Extractor.ConditionType.NONE, ""
            );
        } catch (Extractor.ReservedFieldException e) {
            throw new BadRequestException("Trying to overwrite a reserved message field", e);
        } catch (ConfigurationException e) {
            throw new BadRequestException("Invalid extractor configuration", e);
        }

        final Extractor.Result result = extractor.runExtractor(example);
        final RegexReplaceTesterResponse.Match match = result == null ? null :
                RegexReplaceTesterResponse.Match.create(String.valueOf(result.getValue()), result.getBeginIndex(), result.getEndIndex());
        return RegexReplaceTesterResponse.create(result != null, match, regex, replacement, replaceAll, example);
    }
}
