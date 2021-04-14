/*
 * */
package com.synectiks.process.server.rest.resources.alarmcallbacks;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Maps;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfiguration;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackConfigurationService;
import com.synectiks.process.server.alarmcallbacks.AlarmCallbackFactory;
import com.synectiks.process.server.alarmcallbacks.EmailAlarmCallback;
import com.synectiks.process.server.alerts.AbstractAlertCondition;
import com.synectiks.process.server.alerts.types.DummyAlertCondition;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.database.NotFoundException;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.alarms.callbacks.AlarmCallback;
import com.synectiks.process.server.plugin.alarms.transports.TransportConfigurationException;
import com.synectiks.process.server.plugin.configuration.ConfigurationRequest;
import com.synectiks.process.server.plugin.streams.Stream;
import com.synectiks.process.server.rest.models.alarmcallbacks.AlarmCallbackListSummary;
import com.synectiks.process.server.rest.models.alarmcallbacks.AlarmCallbackSummary;
import com.synectiks.process.server.rest.models.alarmcallbacks.responses.AvailableAlarmCallbackSummaryResponse;
import com.synectiks.process.server.rest.models.alarmcallbacks.responses.AvailableAlarmCallbacksResponse;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;
import com.synectiks.process.server.streams.StreamService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.mail.EmailException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.synectiks.process.server.shared.security.RestPermissions.STREAMS_READ;
import static com.synectiks.process.server.shared.security.RestPermissions.USERS_LIST;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiresAuthentication
@Api(value = "AlarmCallbacks", description = "Manage legacy alarm callbacks (aka alert notifications)")
@Path("/alerts/callbacks")
@Produces(MediaType.APPLICATION_JSON)
public class AlarmCallbacksResource extends RestResource {
    private final AlarmCallbackConfigurationService alarmCallbackConfigurationService;
    private final StreamService streamService;
    private final Set<AlarmCallback> availableAlarmCallbacks;
    private final AlarmCallbackFactory alarmCallbackFactory;

    @Inject
    public AlarmCallbacksResource(AlarmCallbackConfigurationService alarmCallbackConfigurationService,
                                  StreamService streamService,
                                  Set<AlarmCallback> availableAlarmCallbacks,
                                  AlarmCallbackFactory alarmCallbackFactory) {
        this.alarmCallbackConfigurationService = alarmCallbackConfigurationService;
        this.streamService = streamService;
        this.availableAlarmCallbacks = availableAlarmCallbacks;
        this.alarmCallbackFactory = alarmCallbackFactory;
    }

    @GET
    @Timed
    @ApiOperation(value = "Get a list of all alarm callbacks")
    public AlarmCallbackListSummary all() throws NotFoundException {
        final List<AlarmCallbackSummary> alarmCallbacks = streamService.loadAll().stream()
                .filter(stream -> isPermitted(STREAMS_READ, stream.getId()))
                .flatMap(stream -> alarmCallbackConfigurationService.getForStream(stream).stream()
                        .map(callback -> AlarmCallbackSummary.create(
                                callback.getId(),
                                callback.getStreamId(),
                                callback.getType(),
                                callback.getTitle(),
                                callback.getConfiguration(),
                                callback.getCreatedAt(),
                                callback.getCreatorUserId()
                        )))
                .collect(Collectors.toList());

        return AlarmCallbackListSummary.create(alarmCallbacks);
    }

    @GET
    @Path("/types")
    @Timed
    @ApiOperation(value = "Get a list of all alarm callbacks types")
    public AvailableAlarmCallbacksResponse available() {
        final Map<String, AvailableAlarmCallbackSummaryResponse> types = Maps.newHashMapWithExpectedSize(availableAlarmCallbacks.size());
        for (AlarmCallback availableAlarmCallback : availableAlarmCallbacks) {
            final AvailableAlarmCallbackSummaryResponse type = new AvailableAlarmCallbackSummaryResponse();
            type.name = availableAlarmCallback.getName();
            type.requested_configuration = getConfigurationRequest(availableAlarmCallback).asList();
            types.put(availableAlarmCallback.getClass().getCanonicalName(), type);
        }

        final AvailableAlarmCallbacksResponse response = new AvailableAlarmCallbacksResponse();
        response.types = types;

        return response;
    }

    @POST
    @Timed
    @Path("/{alarmCallbackId}/test")
    @ApiOperation(value = "Send a test alert for a given alarm callback")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Alarm callback not found."),
            @ApiResponse(code = 400, message = "Invalid ObjectId."),
            @ApiResponse(code = 500, message = "Error while testing alarm callback")
    })
    @NoAuditEvent("only used to test alert notifications")
    public Response test(@ApiParam(name = "alarmCallbackId", value = "The alarm callback id to send a test alert for.", required = true)
                         @PathParam("alarmCallbackId") String alarmCallbackId) throws TransportConfigurationException, EmailException, NotFoundException {
        final AlarmCallbackConfiguration alarmCallbackConfiguration = alarmCallbackConfigurationService.load(alarmCallbackId);
        final String streamId = alarmCallbackConfiguration.getStreamId();
        checkPermission(RestPermissions.STREAMS_EDIT, streamId);

        final Stream stream = streamService.load(streamId);

        final DummyAlertCondition testAlertCondition = new DummyAlertCondition(stream, null, Tools.nowUTC(), getSubject().getPrincipal().toString(), Collections.emptyMap(), "Test Alert");
        try {
            AbstractAlertCondition.CheckResult checkResult = testAlertCondition.runCheck();
            AlarmCallback alarmCallback = alarmCallbackFactory.create(alarmCallbackConfiguration);
            alarmCallback.call(stream, checkResult);
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage(), e);
        }

        return Response.ok().build();
    }

    /* This is used to add user auto-completion to EmailAlarmCallback when the current user has permissions to list users */
    private ConfigurationRequest getConfigurationRequest(AlarmCallback callback) {
        if (callback instanceof EmailAlarmCallback && isPermitted(USERS_LIST)) {
            return ((EmailAlarmCallback) callback).getEnrichedRequestedConfiguration();
        }

        return callback.getRequestedConfiguration();
    }
}
