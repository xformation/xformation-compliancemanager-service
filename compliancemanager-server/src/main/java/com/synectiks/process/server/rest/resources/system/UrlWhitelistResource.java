/*
 * */
package com.synectiks.process.server.rest.resources.system;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.audit.AuditEventTypes;
import com.synectiks.process.server.audit.jersey.AuditEvent;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.rest.models.system.urlwhitelist.WhitelistCheckRequest;
import com.synectiks.process.server.rest.models.system.urlwhitelist.WhitelistCheckResponse;
import com.synectiks.process.server.rest.models.system.urlwhitelist.WhitelistRegexGenerationRequest;
import com.synectiks.process.server.rest.models.system.urlwhitelist.WhitelistRegexGenerationResponse;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;
import com.synectiks.process.server.system.urlwhitelist.RegexHelper;
import com.synectiks.process.server.system.urlwhitelist.UrlWhitelist;
import com.synectiks.process.server.system.urlwhitelist.UrlWhitelistService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequiresAuthentication
@Api(value = "System/UrlWhitelist")
@Path("/system/urlwhitelist")
@Produces(MediaType.APPLICATION_JSON)
public class UrlWhitelistResource extends RestResource {

    private final UrlWhitelistService urlWhitelistService;
    private final RegexHelper regexHelper;

    @Inject
    public UrlWhitelistResource(final UrlWhitelistService urlWhitelistService, RegexHelper regexHelper) {
        this.urlWhitelistService = urlWhitelistService;
        this.regexHelper = regexHelper;
    }

    @GET
    @Timed
    @ApiOperation(value = "Get url whitelist.")
    @RequiresPermissions(RestPermissions.URL_WHITELIST_READ)
    public UrlWhitelist get() {
        checkPermission(RestPermissions.URL_WHITELIST_READ);
        return urlWhitelistService.getWhitelist();
    }

    @PUT
    @Timed
    @ApiOperation(value = "Update url whitelist.")
    @AuditEvent(type = AuditEventTypes.URL_WHITELIST_UPDATE)
    @Consumes(MediaType.APPLICATION_JSON)
    @RequiresPermissions(RestPermissions.URL_WHITELIST_WRITE)
    public Response put(@ApiParam(name = "whitelist", required = true) @NotNull final UrlWhitelist whitelist) {
        urlWhitelistService.saveWhitelist(whitelist);
        return Response.noContent().build();
    }

    @POST
    @Path("/check")
    @Timed
    @ApiOperation(value = "Check if a url is whitelisted.")
    @NoAuditEvent("Validation only")
    @Consumes(MediaType.APPLICATION_JSON)
    // Checking can be done without any special permission.
    public WhitelistCheckResponse check(@ApiParam(name = "JSON body", required = true)
                             @Valid @NotNull final WhitelistCheckRequest checkRequest) {
        final boolean isWhitelisted = urlWhitelistService.isWhitelisted(checkRequest.url());
        return WhitelistCheckResponse.create(checkRequest.url(), isWhitelisted);
    }

    @POST
    @Path("/generate_regex")
    @Timed
    @ApiOperation(value = "Generates a regex that can be used as a value for a whitelist entry.")
    @NoAuditEvent("Utility function only.")
    @Consumes(MediaType.APPLICATION_JSON)
    public WhitelistRegexGenerationResponse generateRegex(@ApiParam(name = "JSON body", required = true)
    @Valid @NotNull final WhitelistRegexGenerationRequest generationRequest) {
        final String regex;
        if (generationRequest.placeholder() == null) {
            regex = regexHelper.createRegexForUrl(generationRequest.urlTemplate());
        } else {
            regex = regexHelper.createRegexForUrlTemplate(generationRequest.urlTemplate(),
                    generationRequest.placeholder());
        }
        return WhitelistRegexGenerationResponse.create(regex);
    }
}
