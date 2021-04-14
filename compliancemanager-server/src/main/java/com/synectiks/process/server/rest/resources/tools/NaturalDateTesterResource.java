/*
 * */
package com.synectiks.process.server.rest.resources.tools;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.plugin.utilities.date.NaturalDateParser;
import com.synectiks.process.server.shared.rest.resources.RestResource;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotEmpty;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@RequiresAuthentication
@Path("/tools/natural_date_tester")
public class NaturalDateTesterResource extends RestResource {
    private static final Logger LOG = LoggerFactory.getLogger(RegexTesterResource.class);

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> naturalDateTester(@QueryParam("string") @NotEmpty String string) {
        try {
            return new NaturalDateParser().parse(string).asMap();
        } catch (NaturalDateParser.DateNotParsableException e) {
            LOG.debug("Could not parse from natural date: " + string, e);
            throw new WebApplicationException(e, 422);
        }
    }
}
