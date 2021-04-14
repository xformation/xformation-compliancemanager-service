/*
 * */
package com.synectiks.process.common.plugins.views.search.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.synectiks.process.common.plugins.views.search.Search;
import com.synectiks.process.common.plugins.views.search.SearchDomain;
import com.synectiks.process.common.plugins.views.search.SearchExecutionGuard;
import com.synectiks.process.common.plugins.views.search.export.AuditContext;
import com.synectiks.process.common.plugins.views.search.export.AuditingMessagesExporter;
import com.synectiks.process.common.plugins.views.search.export.ChunkedRunner;
import com.synectiks.process.common.plugins.views.search.export.CommandFactory;
import com.synectiks.process.common.plugins.views.search.export.ExportMessagesCommand;
import com.synectiks.process.common.plugins.views.search.export.MessagesExporter;
import com.synectiks.process.common.plugins.views.search.export.MessagesRequest;
import com.synectiks.process.common.plugins.views.search.export.ResultFormat;
import com.synectiks.process.common.plugins.views.search.export.SimpleMessageChunk;
import com.synectiks.process.common.plugins.views.search.views.ViewDTO;
import com.synectiks.process.server.audit.jersey.NoAuditEvent;
import com.synectiks.process.server.plugin.rest.PluginRestResource;
import com.synectiks.process.server.rest.MoreMediaTypes;
import com.synectiks.process.server.shared.rest.resources.RestResource;
import com.synectiks.process.server.shared.security.RestPermissions;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.glassfish.jersey.server.ChunkedOutput;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

@Api(value = "Search/Messages", description = "Simple search returning (matching) messages only, as CSV.")
@Path("/views/search/messages")
@RequiresAuthentication
public class MessagesResource extends RestResource implements PluginRestResource {

    private final CommandFactory commandFactory;
    private final SearchDomain searchDomain;
    private final SearchExecutionGuard executionGuard;
    private final PermittedStreams permittedStreams;
    private final ObjectMapper objectMapper;

    //allow mocking
    Function<Consumer<Consumer<SimpleMessageChunk>>, ChunkedOutput<SimpleMessageChunk>> asyncRunner = ChunkedRunner::runAsync;
    Function<AuditContext, MessagesExporter> messagesExporterFactory;

    @Inject
    public MessagesResource(
            MessagesExporter exporter,
            CommandFactory commandFactory,
            SearchDomain searchDomain,
            SearchExecutionGuard executionGuard,
            PermittedStreams permittedStreams,
            ObjectMapper objectMapper,
            @SuppressWarnings("UnstableApiUsage") EventBus eventBus) {
        this.commandFactory = commandFactory;
        this.searchDomain = searchDomain;
        this.executionGuard = executionGuard;
        this.permittedStreams = permittedStreams;
        this.objectMapper = objectMapper;
        this.messagesExporterFactory = context -> new AuditingMessagesExporter(context, eventBus, exporter);
    }

    @ApiOperation(
            value = "Export messages as CSV",
            notes = "Use this endpoint, if you want to configure export parameters freely instead of relying on an existing Search"
    )
    @POST
    @Produces(MoreMediaTypes.TEXT_CSV)
    @NoAuditEvent("Has custom audit events")
    public ChunkedOutput<SimpleMessageChunk> retrieve(@ApiParam @Valid MessagesRequest rawrequest) {
        final MessagesRequest request = fillInIfNecessary(rawrequest);

        executionGuard.checkUserIsPermittedToSeeStreams(request.streams(), this::hasStreamReadPermission);

        ExportMessagesCommand command = commandFactory.buildFromRequest(request);

        return asyncRunner.apply(chunkConsumer -> exporter().export(command, chunkConsumer));
    }

    private MessagesRequest fillInIfNecessary(MessagesRequest requestFromClient) {
        MessagesRequest request = requestFromClient != null ? requestFromClient : MessagesRequest.withDefaults();

        if (request.streams().isEmpty()) {
            request = request.toBuilder().streams(loadAllAllowedStreamsForUser()).build();
        }
        return request;
    }

    @ApiOperation(value = "Export a search result as CSV")
    @POST
    @Path("{searchId}")
    @Produces(MoreMediaTypes.TEXT_CSV)
    @NoAuditEvent("Has custom audit events")
    public ChunkedOutput<SimpleMessageChunk> retrieveForSearch(
            @ApiParam(value = "ID of an existing Search", name = "searchId") @PathParam("searchId") String searchId,
            @ApiParam(value = "Optional overrides") @Valid ResultFormat formatFromClient) {
        ResultFormat format = emptyIfNull(formatFromClient);

        Search search = loadSearch(searchId, format.executionState());

        ExportMessagesCommand command = commandFactory.buildWithSearchOnly(search, format);

        return asyncRunner.apply(chunkConsumer -> exporter(searchId).export(command, chunkConsumer));
    }

    @ApiOperation(value = "Export a message table as CSV")
    @POST
    @Path("{searchId}/{searchTypeId}")
    @Produces(MoreMediaTypes.TEXT_CSV)
    @NoAuditEvent("Has custom audit events")
    public ChunkedOutput<SimpleMessageChunk> retrieveForSearchType(
            @ApiParam(value = "ID of an existing Search", name = "searchId") @PathParam("searchId") String searchId,
            @ApiParam(value = "ID of a Message Table contained in the Search", name = "searchTypeId") @PathParam("searchTypeId") String searchTypeId,
            @ApiParam(value = "Optional overrides") @Valid ResultFormat formatFromClient) {
        ResultFormat format = emptyIfNull(formatFromClient);

        Search search = loadSearch(searchId, format.executionState());

        ExportMessagesCommand command = commandFactory.buildWithMessageList(search, searchTypeId, format);

        return asyncRunner.apply(chunkConsumer -> exporter(searchId, searchTypeId).export(command, chunkConsumer));
    }

    private MessagesExporter exporter() {
        return exporter(null, null);
    }

    private MessagesExporter exporter(String searchId) {
        return exporter(searchId, null);
    }

    private MessagesExporter exporter(String searchId, String searchTypeId) {
        return messagesExporterFactory.apply(new AuditContext(userName(), searchId, searchTypeId));
    }

    private String userName() {
        return Objects.requireNonNull(getCurrentUser()).getName();
    }

    private ResultFormat emptyIfNull(ResultFormat format) {
        return format == null ? ResultFormat.empty() : format;
    }

    private Search loadSearch(String searchId, Map<String, Object> executionState) {
        Search search = searchDomain.getForUser(searchId, getCurrentUser(), this::hasViewReadPermission)
                .orElseThrow(() -> new NotFoundException("Search with id " + searchId + " does not exist"));

        search = search.addStreamsToQueriesWithoutStreams(this::loadAllAllowedStreamsForUser);

        search = search.applyExecutionState(objectMapper, executionState);

        executionGuard.check(search, this::hasStreamReadPermission);

        return search;
    }

    private boolean hasViewReadPermission(ViewDTO view) {
        return isPermitted(ViewsRestPermissions.VIEW_READ, view.id());
    }

    private ImmutableSet<String> loadAllAllowedStreamsForUser() {
        return permittedStreams.load(this::hasStreamReadPermission);
    }

    private boolean hasStreamReadPermission(String streamId) {
        return isPermitted(RestPermissions.STREAMS_READ, streamId);
    }
}
