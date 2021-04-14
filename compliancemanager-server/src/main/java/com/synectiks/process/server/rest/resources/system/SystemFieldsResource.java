/*
 * */
package com.synectiks.process.server.rest.resources.system;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.synectiks.process.server.indexer.IndexSetRegistry;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Map;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RequiresAuthentication
@Api(value = "System/Fields", description = "Get list of message fields that exist.")
@Path("/system/fields")
public class SystemFieldsResource extends RestResource {
    private final Indices indices;
    private final IndexSetRegistry indexSetRegistry;

    @Inject
    public SystemFieldsResource(Indices indices, IndexSetRegistry indexSetRegistry) {
        this.indices = indices;
        this.indexSetRegistry = indexSetRegistry;
    }

    @GET
    @Timed
    @ApiOperation(value = "Get list of message fields that exist",
            notes = "This operation is comparably fast because it reads directly from the indexer mapping.")
    @RequiresPermissions(RestPermissions.FIELDNAMES_READ)
    @Produces(APPLICATION_JSON)
    public Map<String, Set<String>> fields(@ApiParam(name = "limit", value = "Maximum number of fields to return. Set to 0 for all fields.", required = false)
                                           @QueryParam("limit") int limit) {
        boolean unlimited = limit <= 0;

        final String[] writeIndexWildcards = indexSetRegistry.getIndexWildcards();

        final Set<String> fields;
        if (unlimited) {
            fields = indices.getAllMessageFields(writeIndexWildcards);
        } else {
            fields = Sets.newHashSetWithExpectedSize(limit);
            addStandardFields(fields);
            int i = 0;
            for (String field : indices.getAllMessageFields(writeIndexWildcards)) {
                if (i == limit) {
                    break;
                }

                fields.add(field);
                i++;
            }
        }
        return ImmutableMap.of("fields", fields);
    }

    private void addStandardFields(Set<String> fields) {
        fields.add(Message.FIELD_SOURCE);
        fields.add(Message.FIELD_MESSAGE);
        fields.add(Message.FIELD_TIMESTAMP);
    }
}
