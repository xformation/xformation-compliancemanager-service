/*
 * */
package com.synectiks.process.common.plugins.views.search.rest;

import com.google.common.collect.ImmutableSet;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.indexer.fieldtypes.MappedFieldTypesService;
import com.synectiks.process.server.plugin.rest.PluginRestResource;
import com.synectiks.process.server.shared.rest.exceptions.MissingStreamPermissionException;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Set;
import java.util.stream.Collectors;

@Api(value = "Field Types")
@Path("/views/fields")
@Produces(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class FieldTypesResource extends RestResource implements PluginRestResource {
    private static final Logger LOG = LoggerFactory.getLogger(FieldTypesResource.class);
    private final MappedFieldTypesService mappedFieldTypesService;
    private final PermittedStreams permittedStreams;

    @Inject
    public FieldTypesResource(MappedFieldTypesService mappedFieldTypesService,
                              PermittedStreams permittedStreams) {
        this.mappedFieldTypesService = mappedFieldTypesService;
        this.permittedStreams = permittedStreams;
    }

    @GET
    @ApiOperation(value = "Retrieve the list of all fields present in the system")
    public Set<MappedFieldTypeDTO> allFieldTypes() {
        return mappedFieldTypesService.fieldTypesByStreamIds(permittedStreams.load(this::allowedToReadStream));
    }

    private boolean allowedToReadStream(String streamId) {
        return isPermitted(RestPermissions.STREAMS_READ, streamId);
    }

    @POST
    @ApiOperation(value = "Retrieve the field list of a given set of streams")
    @NoAuditEvent("This is not changing any data")
    public Set<MappedFieldTypeDTO> byStreams(FieldTypesForStreamsRequest request) {
        checkStreamPermission(request.streams());

        return mappedFieldTypesService.fieldTypesByStreamIds(request.streams());
    }

    private void checkStreamPermission(Set<String> streamIds) {
        Set<String> notPermittedStreams = streamIds.stream().filter(s -> !isPermitted(RestPermissions.STREAMS_READ, s))
                .collect(Collectors.toSet());
        if (!notPermittedStreams.isEmpty()) {
            LOG.info("Not authorized to access resource id <{}>. User <{}> is missing permission <{}:{}>",
                    streamIds, getSubject().getPrincipal(), RestPermissions.STREAMS_READ, streamIds);
            throw new MissingStreamPermissionException("Not authorized to access streams.",
                    streamIds);
        }
    }
}
