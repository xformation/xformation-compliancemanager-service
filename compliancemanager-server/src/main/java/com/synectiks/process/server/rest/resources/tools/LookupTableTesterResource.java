/*
 * */
package com.synectiks.process.server.rest.resources.tools;

import com.codahale.metrics.annotation.Timed;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.lookup.LookupTableService;
import com.synectiks.process.server.plugin.lookup.LookupResult;
import com.synectiks.process.server.rest.models.tools.requests.LookupTableTestRequest;
import com.synectiks.process.server.rest.resources.tools.responses.LookupTableTesterResponse;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@RequiresAuthentication
@Path("/tools/lookup_table_tester")
@Produces(MediaType.APPLICATION_JSON)
public class LookupTableTesterResource extends RestResource {

    private final LookupTableService lookupTableService;

    @Inject
    public LookupTableTesterResource(final LookupTableService lookupTableService) {
        this.lookupTableService = lookupTableService;
    }

    @GET
    @Timed
    public LookupTableTesterResponse grokTest(@QueryParam("lookup_table_name") @NotEmpty String lookupTableName,
                                              @QueryParam("string") @NotEmpty String string) {
        return doTestLookupTable(string, lookupTableName);
    }

    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @NoAuditEvent("only used to test lookup tables")
    @RequiresPermissions(RestPermissions.LOOKUP_TABLES_READ)
    public LookupTableTesterResponse testLookupTable(@Valid @NotNull LookupTableTestRequest lookupTableTestRequest) {
        return doTestLookupTable(lookupTableTestRequest.string(), lookupTableTestRequest.lookupTableName());
    }

    private LookupTableTesterResponse doTestLookupTable(String string, String lookupTableName) {
        if (!lookupTableService.hasTable(lookupTableName)) {
            return LookupTableTesterResponse.error("Lookup table <" + lookupTableName + "> doesn't exist");
        }

        final LookupTableService.Function table = lookupTableService.newBuilder().lookupTable(lookupTableName).build();
        final LookupResult result = table.lookup(string.trim());

        if (result == null) {
            return LookupTableTesterResponse.emptyResult(string);
        }

        return LookupTableTesterResponse.result(string, result);
    }
}
